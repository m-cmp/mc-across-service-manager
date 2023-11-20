#!/bin/bash
sudo apt-get update -y
sudo apt-get install -y curl dirmngr gnupg apt-transport-https ca-certificates software-properties-common

# Add the MongoDB GPG key and repository.
curl -fsSL https://pgp.mongodb.com/server-7.0.asc |  sudo gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg --dearmor
echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list

# Update package list and install MongoDB.
sudo apt-get update -y
sudo apt-get install -y mongodb-org

