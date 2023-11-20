#!/bin/bash

IP1=$1
IP2=$2
IP3=$3

ansible-playbook -i $IP1, $BASE_DIR/ansible/across-service/activation-db.yml
ansible-playbook -i $IP2, $BASE_DIR/ansible/across-service/activation-web.yml -e "mongo_host=$IP3"


