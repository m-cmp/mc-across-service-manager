#!/bin/bash

my_cnf=$(sudo cat /etc/mysql/my.cnf | grep -v "^#" | grep -v "^$")

if ! echo "$my_cnf" | grep -q "default-time-zone"; then
  my_cnf="$my_cnf
[mysqld]
default-time-zone='+9:00'
"
sudo echo "$my_cnf" > /etc/mysql/my.cnf
fi

sudo chmod +w /etc/mysql/mariadb.conf.d/50-server.cnf
sudo chown $USER /etc/mysql/mariadb.conf.d/50-server.cnf
server_conf=$(sudo cat /etc/mysql/mariadb.conf.d/50-server.cnf)
server_conf=$(echo "$server_conf" | sed "s/bind-address            = 127.0.0.1/bind-address            = 0.0.0.0/g")
sudo echo "$server_conf" > /etc/mysql/mariadb.conf.d/50-server.cnf

sudo systemctl restart mariadb

mysql -u mcmp -p"mcmp" << EOF
DROP DATABASE IF EXISTS inventory;
CREATE DATABASE inventory;
USE inventory;
EOF

mysql -u mcmp -p"mcmp" inventory < inventory.sql
