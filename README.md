# GitLab CI/CD Pipeline & Infrastructure Automation

> DevOps portfolio project — a fully automated pipeline covering build, containerisation, infrastructure provisioning, configuration management, and monitoring. A Java Spring Boot app serves as the deployed workload; the focus is entirely on the pipeline and infrastructure.
>
> **From a git push to a fully running environment in under 5 minutes:** a fresh AWS EC2 instance is provisioned, Cloudflare DNS A records are created, the application is reachable at `app.udris.dev`, and Grafana dashboards are live at `grafana.udris.dev` — all without a single manual step.

---

## Table of contents

- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [CI/CD pipeline](#cicd-pipeline)
- [Infrastructure](#infrastructure)
- [Configuration management](#configuration-management)
- [Monitoring](#monitoring)
- [Prerequisites & running the pipeline](#prerequisites--running-the-pipeline)

---

## Tech stack

| Area | Tools |
|---|---|
| CI/CD | GitLab CI/CD |
| Containerisation | Docker, Docker Compose |
| Infrastructure as Code | Terraform, AWS EC2 |
| DNS | Cloudflare (proxied, Full Strict TLS) |
| Config management | Ansible |
| Monitoring | Prometheus, Grafana, Node Exporter |
| Reverse proxy | NGINX + Cloudflare Origin Certificates |
| Registry | GitLab Container Registry |

---

## Architecture

A git push triggers the pipeline in GitLab. The pipeline moves through six ordered stages — from compiling and testing the application, through building and publishing a Docker image, provisioning cloud infrastructure with Terraform, configuring the server with Ansible, and finally deploying the full stack via Docker Compose.

```
Git push
    │
    ▼
┌─────────┐    ┌─────────┐    ┌─────────┐    ┌──────────────┐    ┌──────────┐    ┌─────────┐
│  test   │──▶│  build  │──▶│ docker  │──▶│    infra     │──▶│  config  │──▶│ deploy  │
│         │    │         │    │         │    │ (Terraform)  │    │(Ansible) │    │(Ansible)│
└─────────┘    └─────────┘    └─────────┘    └──────────────┘    └──────────┘    └─────────┘
                                                    │
                                            EC2 IP → artifact
                                            Cloudflare DNS A record
```

All infrastructure lives on a single AWS EC2 instance. The application, Prometheus, Grafana, and Node Exporter run as Docker Compose services on that host. NGINX terminates TLS in front of the app and Grafana, using Cloudflare Origin Certificates with Full Strict mode.

Infrastructure code, Ansible playbooks, and pipeline definition all live in this repository.

---

## CI/CD pipeline

Defined in [`.gitlab-ci.yml`](.gitlab-ci.yml). Six stages run sequentially; one additional manual job is available for teardown.

### `test` — Verify the application builds cleanly

Runs Maven unit tests inside a Maven Docker image. JUnit reports are published as pipeline artifacts, giving visibility into test results directly in the GitLab UI without needing to inspect logs.

### `build` — Produce the application JAR

Runs `mvn clean package -DskipTests` and passes the compiled `target/*.jar` as a pipeline artifact to the next stage. Keeping build and test as separate jobs makes failures easier to isolate.

### `docker` — Build and publish the container image

Uses Docker-in-Docker to build the application image and push it to the GitLab Container Registry. The image is tagged with `CI_COMMIT_SHA` by default, giving each pipeline run a unique, traceable image. Registry authentication uses the built-in `CI_REGISTRY_*` variables — no credentials are hardcoded.

### `infra` — Provision cloud infrastructure with Terraform

Runs `terraform init` + `terraform apply -auto-approve` in the `terraform/` directory. Creates an AWS EC2 instance, security group (SSH/HTTP/HTTPS), SSH key pair, and a Cloudflare proxied DNS A record. The EC2 public IP is written to `terraform/ec2_ip.txt` and exposed as a pipeline artifact so downstream jobs can consume it without manual coordination.

A manual `terraform-destroy` job is also available in this stage for tearing down the environment from the GitLab UI.

### `config` — Provision the server with Ansible

Reads the EC2 IP from the Terraform artifact and runs `ansible/setup.yml` against the fresh instance. Installs Docker, configures NGINX with Cloudflare TLS certificates, creates a swap file, and adds users to the Docker group. This stage is idempotent — re-running it on an already-configured host is safe.

### `deploy` — Deploy the application stack

Runs `ansible/deploy-app.yml` to copy the Docker Compose project to the server, render the environment file from variables, authenticate with the container registry, and bring the stack up with `docker compose pull && docker compose up -d`. Prometheus, Grafana, and Node Exporter start alongside the application in the same Compose stack.

---

## Infrastructure

Managed by Terraform in the `terraform/` directory.

**AWS (main.tf)**
- EC2 instance (instance type and AMI configurable via variables)
- Security group: allows inbound SSH (22), HTTP (80), HTTPS (443)
- SSH key pair provisioned from a public key variable

**Cloudflare (dns.tf)**
- Proxied A records for `app.` and `grafana.` subdomains pointing to the EC2 public IP
- Proxy enabled — traffic passes through Cloudflare's network, TLS terminates at NGINX using an Origin Certificate

Key Terraform variables: `aws_region`, `ami_id`, `instance_type`, `public_key`, `cloudflare_api_token`, `cloudflare_zone_id`.

---

## Configuration management

Two Ansible playbooks in `ansible/`:

**`setup.yml` — Server provisioning**

Runs four roles in order:

| Role | What it does |
|---|---|
| `swap` | Creates and enables a swap file |
| `docker` | Installs Docker Engine, adds users to the `docker` group |
| `nginx` | Installs NGINX, deploys reverse proxy configs for the app and Grafana |
| `ssl` | Places Cloudflare Origin Certificate + key under `/etc/nginx/ssl`, restarts NGINX |

**`deploy-app.yml` — Application deployment**

Copies `ansible/files/blog-app/` (Docker Compose project + Prometheus config) to the server, renders a `.env` file from CI variables via template, authenticates with the GitLab Container Registry, and runs `docker compose up -d`. Sensitive values are stored in `ansible/group_vars/vault.yml` (Ansible Vault) and unlocked at runtime via `ANSIBLE_VAULT_PASSWORD`.

---

## Monitoring

Prometheus and Grafana run as services in the same Docker Compose stack as the application.

**Prometheus scrape targets** (see `ansible/files/blog-app/prometheus/prometheus.yml`):

| Job | Target | What it monitors |
|---|---|---|
| `node-exporter` | `:9100` | EC2 system metrics (CPU, memory, disk, network) |
| `blog-app` | `:8080/actuator/prometheus` | Spring Boot JVM and application metrics |

**Grafana dashboards** (provisioned from `grafana/dashboards/`):
- `ec2-system.json` — host-level infrastructure metrics
- `spring-boot.json` — JVM heap, GC, HTTP request rates

Grafana is exposed via NGINX at `grafana.<domain>` with Cloudflare proxy + TLS.

---

## Prerequisites & running the pipeline

**Local tools** (for development or running Terraform/Ansible outside CI):

```
git, mvn, docker, terraform, ansible
```

**CI/CD environment variables** — set these in GitLab → Settings → CI/CD → Variables:

| Variable | Purpose |
|---|---|
| `CI_REGISTRY`, `CI_REGISTRY_USER`, `CI_REGISTRY_PASSWORD`, `CI_REGISTRY_IMAGE` | GitLab Container Registry (predefined, auto-injected) |
| `CI_DEPLOY_USER`, `CI_DEPLOY_PASSWORD` | Registry credentials used by Ansible on the remote host |
| `SSH_PRIVATE_KEY_B64` | Base64-encoded SSH private key for Ansible to reach EC2 |
| `ANSIBLE_VAULT_PASSWORD` | Unlocks `ansible/group_vars/vault.yml` |
| `CLOUDFLARE_CERT`, `CLOUDFLARE_KEY` | Cloudflare Origin Certificate and private key |
| `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` | AWS credentials for Terraform |
| `TF_VAR_cloudflare_api_token`, `TF_VAR_cloudflare_zone_id` | Passed to Terraform as variables |
| `TF_VAR_ami_id`, `TF_VAR_public_key` | EC2 AMI and SSH public key for Terraform |

**Triggering the pipeline:**

Push a commit to the repository — all stages run automatically in order. The `terraform-destroy` job must be triggered manually from the GitLab pipeline UI when the environment is no longer needed.

**Runner requirements:**

The pipeline expects a runner tagged `home-runner`. The `docker` stage requires Docker-in-Docker support (`docker:dind` service).

---

> Pipeline screenshots are available in `docs/images/` — one per stage, plus a Grafana dashboard screenshot.
