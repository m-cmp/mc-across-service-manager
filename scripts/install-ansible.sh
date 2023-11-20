#!/bin/bash

# Install Ansible
sudo apt update -y
sudo apt-add-repository ppa:ansible/ansible

sudo apt update -y
sudo apt install ansible -y

# Ansible configuration
if [ ! -d ansible ]; then
  mkdir $BASE_DIR/ansible
  echo "Create ansible dir"
fi

#cd $BASE_DIR/ansible
# sudo ansible-config init --disabled -t all > ${BASE_DIR}/ansible/ansible.cfg

ansible --version
