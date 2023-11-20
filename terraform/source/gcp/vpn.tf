#vpn gateway
resource "google_compute_vpn_gateway" "vpn_gateway" {
  name    = "vpngateway"
  network = google_compute_network.vpc.id
}

resource "google_compute_forwarding_rule" "fr_esp" {
  name        = "fr-esp"
  ip_protocol = "ESP"
  ip_address  = google_compute_address.tunnel_ip.address
  target      = google_compute_vpn_gateway.vpn_gateway.id
}

resource "google_compute_forwarding_rule" "fr_udp500" {
  name        = "fr-udp500"
  ip_protocol = "UDP"
  port_range  = "500"
  ip_address  = google_compute_address.tunnel_ip.address
  target      = google_compute_vpn_gateway.vpn_gateway.id
}

resource "google_compute_forwarding_rule" "fr_udp4500" {
  name        = "fr-udp4500"
  ip_protocol = "UDP"
  port_range  = "4500"
 ip_address  = google_compute_address.tunnel_ip.address
  target      = google_compute_vpn_gateway.vpn_gateway.id
}


#vpn tunnel
resource "google_compute_vpn_tunnel" "vpn_tunnel" {
  name          = "vpn-tunnel"
  peer_ip       = var.customer_tunnel_ip 
  shared_secret = "12345678"
  local_traffic_selector= ["0.0.0.0/0"]

  target_vpn_gateway = google_compute_vpn_gateway.vpn_gateway.id

  depends_on = [
    google_compute_forwarding_rule.fr_esp,
    google_compute_forwarding_rule.fr_udp500,
    google_compute_forwarding_rule.fr_udp4500,
  ]
}


#vpn route
resource "google_compute_route" "vpnroute" {
  name       = "vpnroute"
  network    = google_compute_network.vpc.name
  dest_range = var.customer_ip_address_space
  priority   = 1000

  next_hop_vpn_tunnel = google_compute_vpn_tunnel.vpn_tunnel.id
}


variable "customer_tunnel_ip" {
  type    = string
  default = "3.37.0.12"
}

variable "customer_ip_address_space" {
  type = string
  default = "10.2.0.0/22"
}