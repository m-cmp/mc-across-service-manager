#network_interface
resource "azurerm_network_interface" "nic" {
  name                = "nic"
  location            = data.azurerm_resource_group.tech4.location
  resource_group_name = data.azurerm_resource_group.tech4.name
  ip_configuration {
    name                          = "internal"
    subnet_id                     = azurerm_subnet.subnet.id
    private_ip_address_allocation = "Dynamic"
    public_ip_address_id = azurerm_public_ip.public_ip.id
  }
}

#공용 IP(ssh)
resource "azurerm_public_ip" "public_ip" {
  name                = "public_ip"
  resource_group_name = data.azurerm_resource_group.tech4.name
  location            = data.azurerm_resource_group.tech4.location
  allocation_method   = "Static"
  sku = "Standard"
}


#security_group
resource "azurerm_network_security_group" "security_group" {
  name                = "security_group"
  location            = data.azurerm_resource_group.tech4.location
  resource_group_name = data.azurerm_resource_group.tech4.name

    security_rule {
    name                       = "ssh"
    priority                   = 100
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "Tcp"
    source_port_range          = "*"
    destination_port_range     = "22"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }

  security_rule {
    name                       = "icmp"
    priority                   = 200
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "Icmp"
    source_port_range          = "*"
    destination_port_range     = "*"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }

   security_rule {
    name                       = "http"
    priority                   = 300
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "Tcp"
    source_port_range          = "*"
    destination_port_range     = "80"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }
  
   security_rule {
    name                       = "ansible1"
    priority                   = 310
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "Tcp"
    source_port_range          = "*"
    destination_port_range     = "3000"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }
   security_rule {
    name                       = "ansible2"
    priority                   = 320
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "Tcp"
    source_port_range          = "*"
    destination_port_range     = "5000"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }
   security_rule {
    name                       = "ansible3"
    priority                   = 330
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "Tcp"
    source_port_range          = "*"
    destination_port_range     = "27017"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }
}


resource "azurerm_network_interface_security_group_association" "sg_association" {
  network_interface_id = azurerm_network_interface.nic.id
  network_security_group_id = azurerm_network_security_group.security_group.id
}

#virtual machine
resource "azurerm_linux_virtual_machine" "azure" {
  name                = var.instance_name
  resource_group_name = data.azurerm_resource_group.tech4.name
  location            = data.azurerm_resource_group.tech4.location
  size                = "Standard_B2s"
  admin_username      = "ubuntu"

  admin_ssh_key {
    username   = "ubuntu"
    # public_key = "ssh-rsa A ~" or public_key = file("~/root/username.pub")
    public_key = "{yours}"
  }
  zone = "2"

  network_interface_ids = [
    azurerm_network_interface.nic.id
  ]

  os_disk {
    caching              = "ReadWrite"
    storage_account_type = "Standard_LRS"
  }

 source_image_reference {
    publisher = "Canonical"
    offer     = "0001-com-ubuntu-server-focal"
    sku       = "20_04-lts"
    version   = "latest"
  }

  depends_on = [
    azurerm_network_interface.nic
  ]
}


variable "instance_name" {
  type    = string
  default = "azure"
}

output "instance_id" {
  value = azurerm_linux_virtual_machine.azure.id
}

output "instance_name" {
  value = azurerm_linux_virtual_machine.azure.name
}

output "public_ip" {
  value = azurerm_linux_virtual_machine.azure.public_ip_address
}

output "private_ip" {
  value = azurerm_linux_virtual_machine.azure.private_ip_address
}

output "memory_type" {
  value = azurerm_linux_virtual_machine.azure.size
}

