#라우팅 전파
resource "aws_vpn_gateway_route_propagation" "propagation" {
  vpn_gateway_id = aws_vpn_gateway.vpn_gateway.id
  route_table_id = aws_route_table.route_table.id
}


#customer gateway
resource "aws_customer_gateway" "cgw" {
  bgp_asn    = 65000
  ip_address = var.customer_tunnel_ip
  type       = "ipsec.1"

  tags = {
    Name = "cgw"
  }
}


#가상 프라이빗 게이트웨이
resource "aws_vpn_gateway" "vpn_gateway" {
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "test"
  }
}


# Site-to-Site connection
resource "aws_vpn_connection" "sitetosite" {
  vpn_gateway_id      = aws_vpn_gateway.vpn_gateway.id
  customer_gateway_id = aws_customer_gateway.cgw.id
  type                = "ipsec.1"
  static_routes_only  = true
  tunnel1_preshared_key = "12345678"
}


resource "aws_vpn_connection_route" "office" {
  destination_cidr_block = var.customer_ip_address_space
  vpn_connection_id      = aws_vpn_connection.sitetosite.id
}


variable "customer_tunnel_ip" {
  type    = string
  default = "34.64.220.26"
}

variable "customer_ip_address_space" {
  type = string
  default = "10.0.0.0/22"
}


output "tunnel_ip" {
  value = aws_vpn_connection.sitetosite.tunnel1_address
}