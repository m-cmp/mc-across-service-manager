#호스팅 영역
resource "aws_route53_zone" "doamin" {
  name = var.domain
}

#상태 검사
resource "aws_route53_health_check" "aws_health_check" {
  ip_address        = aws_eip.eip.public_ip
  port              = 80
  type              = "HTTP"
  resource_path     = "/"
  failure_threshold = "3"
  request_interval  = "30"

  tags = {
    Name = "aws"
  }
}

resource "aws_route53_health_check" "customer_health_check" {
  ip_address        = var.customer_public_ip
  port              = 80
  type              = "HTTP"
  resource_path     = "/"
  failure_threshold = "3"
  request_interval  = "30"

  tags = {
    Name = "customer"
  }
}

#aws_public_ip 등록
resource "aws_route53_record" "aws_record" {
  zone_id = aws_route53_zone.doamin.id
  name    = "gslb"
  type    = "A"
  ttl     = 30

  weighted_routing_policy {
    weight = 10
  }

  set_identifier = "aws"
  records        = [aws_eip.eip.public_ip]

  health_check_id = aws_route53_health_check.aws_health_check.id
}

#customer_public_ip 등록
resource "aws_route53_record" "customer_record" {
  zone_id = aws_route53_zone.doamin.id
  name    = "gslb"
  type    = "A"
  ttl     = aws_route53_record.aws_record.ttl

  weighted_routing_policy {
    weight = 10
  }

  set_identifier = "customer"
  records        = [var.customer_public_ip]

  health_check_id = aws_route53_health_check.customer_health_check.id
}


variable "domain" {
  type = string
  default = "namu.com"
}

variable "customer_public_ip" {
    type = string
    default = "20.200.212.20"
}

output "domain" {
  value = "${aws_route53_record.aws_record.name}.${var.domain}"
}

output "gslb_weight" {
  value = aws_route53_record.aws_record.weighted_routing_policy.0.weight
}

output "customer_gslb_weight" {
  value = aws_route53_record.customer_record.weighted_routing_policy.0.weight
}