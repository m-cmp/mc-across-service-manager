#instance
resource "google_compute_instance" "gcp_instance" {
  name = var.instance_name
  machine_type = "e2-medium"
  zone         = "asia-northeast3-a"

metadata = {
    # ex: ssh-keys = "ubuntu:ssh-rsa AA~" or ssh-keys = "username:${file("username.pub")}"
    ssh-keys = "{yours}"
}

  boot_disk {
    auto_delete = true
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2004-lts"
      size = 20
    }
  }

  network_interface {
    access_config {
      // Ephemeral public IP
    }

    subnetwork = google_compute_subnetwork.subnet.id
  }
}


variable "instance_name" {
  type    = string
  default = "gcp"
}

output "instance_id" {
  value = google_compute_instance.gcp_instance.instance_id
}

output "instance_name" {
  value = google_compute_instance.gcp_instance.name
}

output "instance_status" {
  value = google_compute_instance.gcp_instance.current_status
}
output "public_ip" {
  value = google_compute_instance.gcp_instance.network_interface.0.access_config.0.nat_ip
}

output "private_ip" {
  value = google_compute_instance.gcp_instance.network_interface.0.network_ip
}

output "memory_type" {
  value = google_compute_instance.gcp_instance.machine_type
}

output "gcp_healthcheck_flag" {
  value = google_compute_instance.gcp_instance.project
}

