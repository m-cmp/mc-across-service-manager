#!/bin/bash

# 업데이트 및 필요 패키지 설치
sudo yum update -y
sudo yum install epel-release -y
sudo yum install curl -y

# NodeSource 저장소 설정

sudo yum install -y gcc-c++ make 
curl -sL https://rpm.nodesource.com/setup_16.x | sudo -E bash - 
sudo yum install -y nodejs

# install pm2
npm install pm2 -g

#install python3
sudo yum update -y
