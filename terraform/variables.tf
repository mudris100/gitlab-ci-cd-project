variable "aws_region" {
  description = "AWS region to deploy resources"
}

variable "ami_id" {
  description = "AMI ID for the Ubuntu image"
}

variable "instance_type" {
  default = "t3.micro"
}
variable "root_volume_size" {
  type    = number
  default = 10
}

variable "owner" {
  default = "Martins"
}

variable "public_key" {
  description = "SSH public key content"
}

variable "cidr_blocks" {
  default = "0.0.0.0/0"
}

variable "ssh_cidr" {
  default = "0.0.0.0/0"
}

variable "cloudflare_api_token" {
  description = "Cloudflare API token"
  sensitive   = true
}

variable "cloudflare_zone_id" {
  description = "Zone ID for udris.dev"
  sensitive   = true
}
