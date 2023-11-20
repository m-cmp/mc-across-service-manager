#!/bin/bash

IP1=$1 
IP2=$2
IP3=$3
IP4=$4


ansible-playbook -i $IP1, $BASE_DIR/ansible/monitoring/service/activation-telegraf.yml -e "PING_TARGET_URLS=$IP2 NET_RESPONSE_ADDRESS=localhost:27017"
ansible-playbook -i $IP3, $BASE_DIR/ansible/monitoring/service/activation-telegraf.yml -e "PING_TARGET_URLS=$IP4 NET_RESPONSE_ADDRESS=localhost:80"

