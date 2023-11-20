#!/bin/bash

cd $HOME
if [ ! -d mcmp ]; then
  mkdir mcmp
  echo "Create mcmp dir"
fi

sudo apt update
sudo apt-get update

#Install Java(OpenJDK 17)
java -version
if [ $? -ne 0 ]; then
  sudo apt install -y openjdk-17-jdk
fi

#Install Gradle
gradle --version
sudo apt install -y unzip
cd $HOME/mcmp
wget -c https://services.gradle.org/distributions/gradle-7.4.2-bin.zip -P /tmp
sudo unzip -d /opt/gradle /tmp/gradle-7.4.2-bin.zip
sudo chown -R $USER:$USER /opt/gradle

# Node 설치
node -v
if [ $? -ne 0 ]; then
  sudo apt-get install -y ca-certificates curl gnupg
  sudo mkdir -p /etc/apt/keyrings
  curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | sudo gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg

  NODE_MAJOR=16
  echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" | sudo tee /etc/apt/sources.list.d/nodesource.list

  sudo apt-get update
  sudo apt-get install -y nodejs
fi

#Install Git
git --version
if [ $? -ne 0 ]; then
  sudo apt-get install -y git
fi

#Install Docker 
#sudo systemctl status docker
docker -v
if [ $? -ne 0 ]; then
  # Install Docker CE
  sudo apt-get install -y apt-transport-https ca-certificates curl gnupg-agent software-properties-common
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
  sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
  sudo apt-get update
  sudo apt-get install -y docker-ce docker-ce-cli containerd.io
fi
