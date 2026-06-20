locals {
  ec2_ip = aws_instance.main.public_ip
  dns_records = {
    "app"     = local.ec2_ip
    "grafana" = local.ec2_ip
  }
}

resource "cloudflare_record" "app_dns" {
  for_each = local.dns_records

  zone_id = var.cloudflare_zone_id
  name    = each.key
  content = each.value
  type    = "A"
  ttl     = 1
  proxied = true
}
