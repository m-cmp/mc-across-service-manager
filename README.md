# M-CMP mc-across-service-manager

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

- 소스 다운로드 (Git clone)
- 필요 패키지/도구 설치
- 환경 변수 설정
- 빌드 및 실행 (shell script)

---

---

## mc-across-service-manager 소스 빌드 및 실행 방법 상세

### (1) 소스 다운로드

- Git 설치
  ```bash
  	sudo apt update
  	sudo apt install git
  ```
- mc-across-service-manager 소스 다운로드
  ```bash
  	mkdir -p ~/mcmp/git
  	cd ~/mcmp/git
  	git clone https://github.com/m-cmp/mc-across-service-manager.git
  	export PROJECT_ROOT=$(pwd)/mc-across-service-manager
  ```

### (2) 필요 패키지/도구 설치

- Java, Gradle, Git, Docker 설치
  ```bash
  	cd $PROJECT_ROOT/scripts
  	sudo chmod +x *
  	. $PROJECT_ROOT/scripts/init-install.sh
  ```
- InfluxDB, MariaDB 설치

  ```bash
  	# Install DBs
  	. $PROJECT_ROOT/scripts/install-dbs.sh

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
    . $PROJECT_ROOT/scripts/install-opensources.sh
  ```

### (3) 환경 변수 설정

- BASE_DIR 설정
  ```bash
  	export BASE_DIR=~/mcmp
  ```
- Java, Gradle, MariaDB, InfluxDB, Ansible, Terraform 환경 변수 설정 스크립트 적용
  ```bash
  	mkdir -p $BASE_DIR/build
  	source $PROJECT_ROOT/scripts/set-env.sh
  ```

### (4) 빌드 및 실행

- Shell Script 실행

  ```bash
  	#Set Link
  	mkdir -p ~/mcmp/
  	. $PROJECT_ROOT/scripts/set-link.sh

  	#Build & Run React Project
  	. $PROJECT_ROOT/scripts/build-web.sh

  	#Build Springboot Project
  	. $PROJECT_ROOT/scripts/build-springboot-all.sh

  	#Run Springboot Project
  	. $PROJECT_ROOT/scripts/deploy-springboot-all.sh

  	#Run Airflow
  	. $PROJECT_ROOT/scripts/run-airflow.sh
  ```

---

---

## mc-across-service-manager 기여 방법

Issues/Discussions/Ideas: Utilize issue of mc-across-service-manager
