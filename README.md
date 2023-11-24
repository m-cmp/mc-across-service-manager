# M-CMP mc-across-service-manager
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fm-cmp%2Fmc-across-service-manager.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fm-cmp%2Fmc-across-service-manager?ref=badge_shield)


This repository provides a Multi-Cloud mc-across-service-manager.

A sub-system of [M-CMP platform](https://github.com/m-cmp/docs/tree/main) to deploy and manage Multi-Cloud Infrastructures.

## Overview

M-CMP의 mc-across-service-manager 서브시스템이 제공하는 기능은 다음과 같다.

- 클라우드간 연계 서비스 운용 및 관리 기능
- 클라우드간 마이그레이션 기능
- 멀티 클라우드 인프라 동적 성능 진단 관리 기능
- 클라우드간 백업 자동화 및 복구 기능

## 목차

1. [mc-across-service-manager 실행 및 개발 환경]
2. [mc-across-service-manager 실행 방법]
3. [mc-across-service-manager 소스 빌드 및 실행 방법 상세]
4. [mc-across-service-manager 기여 방법]

---

---

## mc-across-service-manager 실행 및 개발 환경

- Linux OS (Ubuntu 22.04 LTS)
- Java (Openjdk 17)
- Gradle (v7.4.2)
- MariaDB (v10.11.5)
- InfluxDB (v2.7.3)
- Python (v3.10.12)
- Node.js (v16.20.2)
- npm (v8.19.4)
- docker (v24.0.7)
- git(v2.34.1)
- terraform (v1.5.7)
- ansible (v2.15.5)
- airflow (v2.7.2)

---

---

## mc-across-service-manager 실행 방법

### 소스 코드 기반 설치 및 실행

- 방화벽 설정
- 소스 다운로드 (Git clone)
- 필요 패키지/도구 설치 및 환경 변수 설정
- CSP Credential, SSH Key 설정
- 빌드 및 실행 (shell script)

---

---

## mc-across-service-manager 소스 빌드 및 실행 방법 상세

### (1) 방화벽 TCP 포트 허용 설정

- 80, 443
- 3306 (MariaDB)
- 8086 (InfluxDB)
- 8080 (Airflow)
- 8090, 8091, 8092, 8095 (서브시스템)
- 4173, 5173 (UX/UI)

### (2) 소스 다운로드

- Git 설치
  ```bash
  	sudo apt update
  	sudo apt install -y git
  ```
- mc-across-service-manager 소스 다운로드
  ```bash
  	export BASE_DIR=$HOME/mcmp
  	mkdir -p $BASE_DIR/git
  	cd $BASE_DIR/git
  	git clone https://github.com/m-cmp/mc-across-service-manager.git
  	export PROJECT_ROOT=$(pwd)/mc-across-service-manager
  ```

### (3) 필요 패키지/도구 설치 및 환경 변수 설정

- Java, Gradle, Node.js, npm, Git, Docker 설치

  ```bash
  	cd $PROJECT_ROOT/scripts
  	sudo chmod +x *.sh
  	. $PROJECT_ROOT/scripts/init-install.sh
  	mkdir -p $BASE_DIR/build

  ```

- 환경 변수 설정
  ```bash
  	cd $PROJECT_ROOT/scripts
  	. $PROJECT_ROOT/scripts/set-env.sh
  	source $HOME/.bashrc
  ```
- InfluxDB, MariaDB 설치

  ```bash

  	# Install DBs
  	. $PROJECT_ROOT/scripts/install-dbs.sh
  	y , Enter 입력

  	# Set Secure
  	sudo mysql_secure_installation
  	사용할 root password 입력
  	y
  	n
  	n
  	n
  	n
  	y

  	# Set Auth
  	sudo mysql -u root -p
  	root password 입력

  	# In MariaDB
  	create user 'mcmp'@'%' identified by 'mcmp';
  	grant all privileges on *.* to 'mcmp'@'%' with grant option;
  	flush privileges;
  	exit

  	# Set MariaDB configuration
  	cd $PROJECT_ROOT/scripts
  	sudo ./set-mariadb.sh
  ```

- Terraform, Ansible, Airflow 설치

  ```bash
    #Install Terraform, Ansible, Airflow
    . $PROJECT_ROOT/scripts/install-opensources.sh
    Enter 입력

    #Set Resource File Link
    . $PROJECT_ROOT/scripts/set-link.sh
  ```

### (4) CSP Credential, SSH Key 설정

- CSP Credential 설정

  - GCP는 credential.sh에 정의된 GOOGLE_CLOUD_KEYFILE_JSON 환경 변수에 JSON Key File Path를 값으로 설정하고,
    해당 Path에 GCP JSON Key File이 존재해야 한다.

  ```bash
  	vi $PROJECT_ROOT/scripts/credential.sh

  	"사용자 입력" 대신 credential 정보 입력
  	. $PROJECT_ROOT/scripts/credential.sh

  	# 설정 적용
  	source $HOME/.bashrc
  ```

- CSP SSH Public Key 설정

  - AWS

    ```bash
    	# 수정할 vm.tf 파일
    	vi $BASE_DIR/terraform/source/aws/vm.tf

    	# aws_key_pair의 key_name에 aws ssh public key 이름으로 수정 후 vm.tf 파일 저장
    	data "aws_key_pair" "mcmp_key" {
    	  key_name   = "" #aws 서버에 등록된 ssh키 이름
    	  include_public_key = true
    	}
    ```

  - GCP :

    ```bash
    	# 수정할 vm.tf 파일
    	vi $BASE_DIR/terraform/source/gcp/vm.tf

    	# metadata의 ssh-keys에 gcp ssh public key로 수정 후 vm.tf 파일 저장
    	resource "google_compute_instance" "gcp_instance" {
    	  ...

    	  metadata = {
    		# ex: ssh-keys = "ubuntu:ssh-rsa AA~" or ssh-keys = "username:${file("username.pub")}"
    		ssh-keys = ""
    	  }

    	  ...
    	}
    ```

    ```bash
    	# 수정할 vpc.tf 파일
    	vi $BASE_DIR/terraform/source/gcp/vpc.tf

    	# provider google의 project에 GCP Project ID 입력 후 vpc.tf 파일 저장
    	  provider "google" {
    	    project     = ""
    	    region      = "asia-northeast3"
    	  }
    ```

  - AZURE

    ```bash
    	# 수정할 vm.tf 파일
    	vi $BASE_DIR/terraform/source/azure/vm.tf

    	# admin_ssh_key 내용을 azure ssh key 로 수정 후 vm.tf 파일 저장
    	resource "azurerm_linux_virtual_machine" "azure" {
    	  ...

    	  admin_ssh_key {
    		username   = "ubuntu"
    		# public_key = "ssh-rsa A ~" or public_key = file("/home/.../username.pub")
    		public_key = ""
    	  }

    	  ...
    	}
    ```

### (5) 빌드 및 실행

- Shell Script 실행

  ```bash
  	#Build & Run React Project
  	. $PROJECT_ROOT/scripts/build-web.sh

  	#Build Springboot Project
  	. $PROJECT_ROOT/scripts/build-springboot-all.sh

  	#Run Springboot Project
  	. $PROJECT_ROOT/scripts/deploy-springboot-all.sh

  	#Run Airflow
  	. $PROJECT_ROOT/scripts/run-airflow.sh
  ```

- UI로 접속
  - http://Public_IP주소:4173
  - 템플릿 목록 : http://Public_IP주소:4173/template

---

---

## mc-across-service-manager 기여 방법

Issues/Discussions/Ideas: Utilize issue of mc-across-service-manager


## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fm-cmp%2Fmc-across-service-manager.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fm-cmp%2Fmc-across-service-manager?ref=badge_large)