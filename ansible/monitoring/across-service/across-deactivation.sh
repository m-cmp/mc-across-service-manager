#!/bin/bash

IP1=$1 
IP2=$2

ansible-playbook -i IP1, $BASE_DIR/ansible/monitoring/across-service/deactivation-telegraf.yml
ansible-playbook -i IP2, $BASE_DIR/ansible/monitoring/across-service/deactivation-telegraf.yml

