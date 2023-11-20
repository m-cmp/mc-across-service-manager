#!/bin/bash

sudo yum update -y 

# Add InfluxData repository
cat <<EOF | sudo tee /etc/yum.repos.d/influxdata.repo
[influxdata]
name = InfluxData Repository - Stable
baseurl = https://repos.influxdata.com/stable/\$basearch/main
enabled = 1
gpgcheck = 1
gpgkey = https://repos.influxdata.com/influxdata-archive_compat.key
EOF

# Update yum package index and install telegraf 
sudo yum update -y && sudo yum install telegraf -y

echo "Installation completed."

