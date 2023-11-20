#!/bin/bash

# /etc/yum.repos.d/mongodb-org-4.4.repo create
echo "[mongodb-org-6.0]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/redhat/$releasever/mongodb-org/6.0/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-6.0.asc" | sudo tee /etc/yum.repos.d/mongodb-org-6.0.repo
# MongoDB install
sudo yum install -y mongodb-org
sudo yum update -y


