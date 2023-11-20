#!/bin/bash

IP=$1

ansible-playbook -i $IP, $BASE_DIR/ansible/service/deactivation.yml

