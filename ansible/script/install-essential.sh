#!/bin/bash
declare -A osInfo;
osInfo[/etc/debian_version]="apt"
osInfo[/etc/alpine-release]="apk"
osInfo[/etc/centos-release]="yum"
osInfo[/etc/fedora-release]="dnf"

for f in ${!osInfo[@]}
do
    if [[ -f $f ]];then
        package_manager=${osInfo[$f]}
    fi
done

sudo $package_manager update -y
sudo $package_manager install python3 python3-pip unzip wget -y
sudo $package_manager update -y
pip3 install setuptools pymongo

sudo $package_manager install nginx -y
