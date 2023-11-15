#traffic manager
resource "azurerm_traffic_manager_profile" "traffic_manager" {
  name                   = "namu"
  resource_group_name    = data.azurerm_resource_group.tech4.name
  traffic_routing_method = "Weighted"

  dns_config {
    relative_name = "namu"
    ttl           = 30
  }

  monitor_config {
    protocol                     = "HTTP"
    port                         = 80
    path                         = "/"
    interval_in_seconds          = 30
    timeout_in_seconds           = 9
    tolerated_number_of_failures = 3
  }
}


#azure public_ip 등록
resource "azurerm_traffic_manager_external_endpoint" "azure" {
  name       = "azure"
  profile_id = azurerm_traffic_manager_profile.traffic_manager.id
  weight     = 10
  target     = azurerm_linux_virtual_machine.azure.public_ip_address
}


#customer public_ip 등록
resource "azurerm_traffic_manager_external_endpoint" "customer" {
  name       = "customer"
  profile_id = azurerm_traffic_manager_profile.traffic_manager.id
  weight     = 10
  target     = var.customer_public_ip
}


variable "customer_public_ip" {
  type = string
  default = "52.79.83.189"
}


output "domain" {
  value = azurerm_traffic_manager_profile.traffic_manager.fqdn
}

output "gslb_weight" {
  value = azurerm_traffic_manager_external_endpoint.azure.weight
}

output "customer_gslb_weight" {
  value = azurerm_traffic_manager_external_endpoint.customer.weight
}