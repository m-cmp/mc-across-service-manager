#!/bin/bash

sed -i 's/Name = "vpc"/Name = "vpc'$1'"/g' $2
sed -i 's/Name = "igw"/Name = "igw'$1'"/g' $2
sed -i 's/Name = "public_subnet"/Name = "public_subnet'$1'"/g' $2
sed -i 's/Name = "route_table"/Name = "route_table'$1'"/g' $2
sed -i 's/name        = "security_group"/name        = "security_group'$1'"/g' $2
