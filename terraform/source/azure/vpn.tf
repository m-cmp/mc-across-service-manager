#local network gateway(customer gateway)
resource "azurerm_local_network_gateway" "customer_gateway" {
  name                = "customer_gateway"
  resource_group_name = data.azurerm_resource_group.tech4.name
  location            = data.azurerm_resource_group.tech4.location
  gateway_address     = var.customer_tunnel_ip
  address_space       = var.customer_ip_address_space
}


#connection
resource "azurerm_virtual_network_gateway_connection" "connection" {
  name                = "connection"
  location            = data.azurerm_resource_group.tech4.location
  resource_group_name = data.azurerm_resource_group.tech4.name

  type                       = "IPsec"
  virtual_network_gateway_id = azurerm_virtual_network_gateway.virtualGateway.id
  local_network_gateway_id   = azurerm_local_network_gateway.customer_gateway.id

  shared_key = "12345678"
}


variable "customer_tunnel_ip" {
  type    = string
  default = "34.64.220.26"
}

variable "customer_ip_address_space" {
  type    = list(string)
  default = ["10.0.0.0/22"]
}