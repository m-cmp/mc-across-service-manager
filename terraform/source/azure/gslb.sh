#!/bin/bash

sed -i 's/name                   = "namu"/name                   = "namu'$1'"/g' $2
sed -i 's/relative_name = "namu"/relative_name = "namu'$1'"/g' $2
sed -i 's/name       = "azure"/name       = "azure'$1'"/g' $2
sed -i 's/name       = "customer"/name       = "customer'$1'"/g' $2
