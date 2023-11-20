#VPN Subnet
resource "azurerm_subnet" "GatewaySubnet" {
  name                 = "GatewaySubnet"
  resource_group_name  = data.azurerm_resource_group.tech4.name
  virtual_network_name = azurerm_virtual_network.vpc.name
  address_prefixes     = ["10.2.1.0/24"]
}


#virtual network gateway
resource "azurerm_virtual_network_gateway" "virtualGateway" {
  name                = "virtualGateway"
  location            = data.azurerm_resource_group.tech4.location
  resource_group_name = data.azurerm_resource_group.tech4.name

  type                = "Vpn"
  vpn_type            = "RouteBased"

  active_active       = false
  enable_bgp          = false
  sku                 = "VpnGw2"

  ip_configuration {
    name                          = "vnetGatewayConfig"
    public_ip_address_id          = azurerm_public_ip.tunnel_ip.id
    private_ip_address_allocation = "Dynamic"
    subnet_id                     = azurerm_subnet.GatewaySubnet.id
  }

}