#!/bin/bash

sed -i 's/name                = "vpc"/name                = "vpc'$1'"/g' $2
sed -i 's/name                 = "subnet"/name                 = "subnet'$1'"/g' $2
sed -i 's/name                = "tunnelc_ip"/name                = "tunnelc_ip'$1'"/g' $2

