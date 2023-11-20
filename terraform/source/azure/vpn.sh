#!/bin/bash

sed -i 's/name                = "customer_gateway"/name                = "customer_gateway'$1'"/g' $2
sed -i 's/name                = "connection"/name                = "connection'$1'"/g' $2

