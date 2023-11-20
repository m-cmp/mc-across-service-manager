#!/bin/bash

#Install InfluxDB v2.7.1
Username=admin
Password=adminpass
Organization=mcmp
Bucket=test
Token=mcmp-token

cd ${BASE_DIR}

sudo docker run --name influxdb -p 8086:8086 -d\
          -v $PWD/data:/var/lib/influxdb2 \
          -v $PWD/config:/etc/influxdb2 \
          -v $PWD/scripts:/docker-entrypoint-initdb.d \
          -e DOCKER_INFLUXDB_INIT_MODE=setup \
          -e DOCKER_INFLUXDB_INIT_USERNAME=$Username\
          -e DOCKER_INFLUXDB_INIT_PASSWORD=$Password\
          -e DOCKER_INFLUXDB_INIT_ORG=$Organization\
          -e DOCKER_INFLUXDB_INIT_BUCKET=$Bucket\
          -e DOCKER_INFLUXDB_INIT_RETENTION=1w\
          -e DOCKER_INFLUXDB_INIT_ADMIN_TOKEN=$Token\
          influxdb:2.7.1

#Install MariaDB v10.11.x
sudo apt update
sudo apt upgrade

sudo apt install -y software-properties-common
sudo apt-key adv --fetch-keys 'https://mariadb.org/mariadb_release_signing_key.asc'
sudo add-apt-repository 'deb https://mirrors.xtom.jp/mariadb/repo/10.11/ubuntu jammy main'

sudo apt update
sudo apt install -y mariadb-server

sudo systemctl start mariadb
sudo systemctl enable mariadb

