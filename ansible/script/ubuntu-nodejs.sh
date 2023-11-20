#!/bin/bash

# 업데이트 및 필요 패키지 설치
apt-get update -y 
apt-get install -y build-essential curl

# NodeSource 저장소 설정

curl -fsSL https://deb.nodesource.com/setup_16.x | sudo -E bash -

# Node.js 설치
apt-get install -y nodejs

# install pm2
npm install pm2 -g

