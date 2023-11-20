#!/bin/bash

sed -i 's/name = "vpc"/name = "vpc'$1'"/g' $2
sed -i 's/name          = "subnet"/name          = "subnet'$1'"/g' $2
sed -i 's/name    = "fw"/name    = "fw'$1'"/g' $2
sed -i 's/name          = "tunnelip"/name          = "tunnelip'$1'"/g' $2
