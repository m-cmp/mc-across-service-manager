#!/bin/bash

DBIP=$1
WEBIP=$2

ansible-playbook -i $DBIP, $BASE_DIR/ansible/across-service/db-terminate.yml
ansible-playbook -i $WEBIP, $BASE_DIR/ansible/across-service/web-terminate.yml

