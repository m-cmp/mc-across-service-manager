#!/bin/bash

sed -i 's/name    = "vpngateway"/name    = "vpngateway'$1'"/g' $2
sed -i 's/name          = "vpn-tunnel"/name          = "vpn-tunnel'$1'"/g' $2
sed -i 's/name       = "vpnroute"/name       = "vpnroute'$1'"/g' $2
sed -i 's/name        = "fr-esp"/name        = "fr-esp-'$1'"/g' $2
sed -i 's/name        = "fr-udp500"/name        = "fr-udp500-'$1'"/g' $2
sed -i 's/name        = "fr-udp4500"/name        = "fr-udp4500-'$1'"/g' $2
