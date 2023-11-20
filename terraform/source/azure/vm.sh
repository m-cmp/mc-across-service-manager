#!/bin/bash

sed -i 's/name                = "nic"/name                = "nic'$1'"/g' $2
sed -i 's/name                = "public_ip"/name                = "public_ip'$1'"/g' $2
sed -i 's/name                = "security_group"/name                = "security_group'$1'"/g' $2
