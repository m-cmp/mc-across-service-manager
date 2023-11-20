#!/bin/bash

cd $BASE_DIR

if [ ! -d terraform ]; then
  mkdir -p terraform/services
  echo "Create terraform dir"
fi

cd terraform
sudo apt-get install -y unzip
sudo wget https://releases.hashicorp.com/terraform/1.5.7/terraform_1.5.7_linux_amd64.zip
sudo unzip terraform_1.5.7_linux_amd64.zip
sudo cp terraform /usr/local/bin

