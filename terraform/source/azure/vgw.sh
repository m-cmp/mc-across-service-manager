#!/bin/bash

sed -i 's/name                = "virtualGateway"/name                = "virtualGateway'$1'"/g' $2
sed -i 's/name                          = "vnetGatewayConfig"/name                          = "vnetGatewayConfig'$1'"/g' $2
