#!/bin/bash

IP1=$1
echo IP1
ansible-playbook -i $IP1 $BASE_DIR/ansible/service/activation.yml


