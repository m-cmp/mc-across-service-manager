provider "google" {
# project     = "sp~-"
  project     = "{yours}"
  region      = "asia-northeast3"

}


#vpc
resource "google_compute_network" "vpc" {
  name = "vpc"
  auto_create_subnetworks = false
}


#subnet
resource "google_compute_subnetwork" "subnet" {
  name          = "subnet"
  ip_cidr_range = "10.0.0.0/22"
  region        = "asia-northeast3"
  network       = google_compute_network.vpc.id
}


#firewall
resource "google_compute_firewall" "fw" {
  name    = "fw"
  network = google_compute_network.vpc.id
  
  allow {
    protocol = "icmp"
}

  allow {
    protocol = "tcp"
    ports    = ["22", "80", "3000", "5000", "27017"]
  }
  
  source_ranges = ["0.0.0.0/0"]

}


#vpn 고정 IP
resource "google_compute_address" "tunnel_ip" {
  name          = "tunnelip"
  address_type  = "EXTERNAL"
  region      = "asia-northeast3"
}

output "tunnel_ip" {
  value = google_compute_address.tunnel_ip.address
}

output "vpc_cidr" {
  value = google_compute_subnetwork.subnet.ip_cidr_range
}

output "subnet_cidr" {
  value = google_compute_subnetwork.subnet.ip_cidr_range
}