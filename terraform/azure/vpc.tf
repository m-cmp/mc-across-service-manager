terraform {

  required_providers {
    azurerm = {
      source = "hashicorp/azurerm"
      version = "=3.0.0"
    }
  }
}

provider "azurerm" {

features {}
}


#리소스 그룹
data "azurerm_resource_group" "tech4" {
  name     = "tech4"
}


#vpc
resource "azurerm_virtual_network" "vpc" {
  name                = "vpc"
  address_space       = ["10.2.0.0/22"]
  location            = data.azurerm_resource_group.tech4.location
  resource_group_name = data.azurerm_resource_group.tech4.name
}


#subnet
resource "azurerm_subnet" "subnet" {
  name                 = "subnet"
  resource_group_name  = data.azurerm_resource_group.tech4.name
  virtual_network_name = azurerm_virtual_network.vpc.name
  address_prefixes     = ["10.2.0.0/24"]
}


#VPN 터널 IP
resource "azurerm_public_ip" "tunnel_ip" {
  name                = "tunnelc_ip"
  resource_group_name = data.azurerm_resource_group.tech4.name
  location            = data.azurerm_resource_group.tech4.location
  allocation_method   = "Static"
  sku = "Standard"
}


output "tunnel_ip" {
  value = azurerm_public_ip.tunnel_ip.ip_address
}

output "vpc_cidr" {
  value = azurerm_virtual_network.vpc.address_space.0
}

output "subnet_cidr" {
  value = azurerm_subnet.subnet.address_prefixes.0
}