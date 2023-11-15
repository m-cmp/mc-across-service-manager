-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        11.1.2-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- inventory 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `inventory` /*!40100 DEFAULT CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci */;
USE `inventory`;

-- 테이블 inventory.tb_across_service 구조 내보내기
CREATE TABLE IF NOT EXISTS `tb_across_service` (
  `across_service_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '연계 서비스 id',
  `across_service_name` varchar(50) NOT NULL COMMENT '연계 서비스명',
  `across_status` varchar(100) NOT NULL COMMENT '연계 서비스 상태',
  `across_type` varchar(12) NOT NULL COMMENT '연계 타입 (VPN/GSLB/ETC)',
  `across_cpu_utilization` decimal(5,2) DEFAULT NULL COMMENT '연계 서비스 총 cpu 사용률',
  `across_memory_usage` decimal(5,2) DEFAULT NULL COMMENT '연계 서비스 총 memory 사용량',
  `across_create_date` datetime NOT NULL COMMENT '연계 서비스 생성일시',
  PRIMARY KEY (`across_service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='연계 서비스 정보';

-- 테이블 데이터 inventory.tb_across_service:~0 rows (대략적) 내보내기

-- 테이블 inventory.tb_application 구조 내보내기
CREATE TABLE IF NOT EXISTS `tb_application` (
  `application_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '애플리케이션 id',
  `service_instance_id` varchar(100) NOT NULL COMMENT '서비스 인스턴스 id',
  `application_name` varchar(100) NOT NULL COMMENT '애플리케이션명',
  `application_status` varchar(50) NOT NULL COMMENT '애플리케이션 상태',
  `application_activate_yn` char(1) NOT NULL COMMENT '애플리케이션 활성화 여부(Y/N)',
  `application_create_date` datetime NOT NULL COMMENT '애플리케이션 생성일시',
  PRIMARY KEY (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='애플리케이션 정보';

-- 테이블 데이터 inventory.tb_application:~0 rows (대략적) 내보내기

-- 테이블 inventory.tb_csp_credential 구조 내보내기
CREATE TABLE IF NOT EXISTS `tb_csp_credential` (
  `csp_credential_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'CSP credential ID',
  `csp_type` varchar(2) NOT NULL COMMENT 'CSP 타입(001)',
  `credentials` text NOT NULL COMMENT 'credential 정보(JSON)',
  PRIMARY KEY (`csp_credential_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='CSP credential 정보';

-- 테이블 데이터 inventory.tb_csp_credential:~0 rows (대략적) 내보내기

-- 테이블 inventory.tb_service_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `tb_service_info` (
  `service_id` varchar(32) NOT NULL COMMENT '서비스 id',
  `service_template_id` bigint(20) NOT NULL COMMENT '서비스 템플릿 id',
  `service_status` varchar(100) NOT NULL COMMENT '워크플로우 상태(RUNNING/WAITING/SUCCESS/FAILED/ERROR)',
  `service_create_date` datetime NOT NULL COMMENT '서비스 생성일시',
  PRIMARY KEY (`service_id`),
  KEY `fk_service_info_template` (`service_template_id`),
  CONSTRAINT `fk_service_info_template` FOREIGN KEY (`service_template_id`) REFERENCES `tb_service_template` (`service_template_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='서비스 정보';

-- 테이블 데이터 inventory.tb_service_info:~1 rows (대략적) 내보내기

-- 테이블 inventory.tb_service_instance 구조 내보내기
CREATE TABLE IF NOT EXISTS `tb_service_instance` (
  `service_instance_id` varchar(100) NOT NULL COMMENT '서비스 인스턴스 id',
  `across_service_id` bigint(20) DEFAULT NULL COMMENT '연계 서비스 id',
  `service_id` varchar(32) DEFAULT NULL COMMENT '서비스 id',
  `csp` varchar(5) NOT NULL COMMENT '클라우드 서비스 프로바이더 타입',
  `vm_instance_id` varchar(50) NOT NULL COMMENT 'vm 인스턴스 id',
  `vm_instance_name` varchar(100) DEFAULT NULL COMMENT 'vm 인스턴스명',
  `vm_instance_status` varchar(50) DEFAULT NULL COMMENT 'vm 인스턴스 상태(health)',
  `vm_instance_public_ip` varchar(15) DEFAULT NULL COMMENT 'vm 인스턴스 public ip',
  `vm_instance_private_ip` varchar(15) DEFAULT NULL COMMENT 'vm 인스턴스 private ip',
  `vm_instance_create_date` datetime DEFAULT NULL COMMENT 'vm 인스턴스 생성일시',
  `vm_cpu_utilization` decimal(5,2) DEFAULT NULL COMMENT 'vm cpu 사용률',
  `vm_memory_usage` decimal(5,2) DEFAULT NULL COMMENT 'vm 메모리 사용량',
  `agent_status` varchar(10) DEFAULT NULL COMMENT 'agent 상태(health)',
  `agent_deploy_yn` char(1) DEFAULT NULL COMMENT 'agent 배포 여부(Y/N)',
  `agent_deploy_date` datetime DEFAULT NULL COMMENT 'agent 배포 일시',
  `tfstate_file_name` varchar(50) DEFAULT NULL COMMENT '서비스 템플릿 파일명',
  `tfstate_file_path` varchar(100) DEFAULT NULL COMMENT '서비스 템플릿 파일 경로',
  PRIMARY KEY (`service_instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='서비스 인스턴스 정보';

-- 테이블 데이터 inventory.tb_service_instance:~0 rows (대략적) 내보내기

-- 테이블 inventory.tb_service_template 구조 내보내기
CREATE TABLE IF NOT EXISTS `tb_service_template` (
  `service_template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '서비스 템플릿 id',
  `service_template_name` varchar(50) NOT NULL COMMENT '서비스 템플릿명',
  `service_template_path` varchar(100) NOT NULL COMMENT '서비스 템플릿 파일 경로',
  `service_template_create_date` datetime NOT NULL COMMENT '서비스 템플릿 생성일시',
  PRIMARY KEY (`service_template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='서비스 템플릿 정보';

-- 테이블 데이터 inventory.tb_service_template:~8 rows (대략적) 내보내기
INSERT IGNORE INTO `tb_service_template` (`service_template_id`, `service_template_name`, `service_template_path`, `service_template_create_date`) VALUES
	(1, 'test1', '/static/template.json', '0000-00-00 00:00:00'),
	(2, 'test2', '/static/template2.json', '0000-00-00 00:00:00'),
	(3, 'test3', '/static/template3.json', '2023-09-26 17:46:11'),
	(4, 'test4', '/static/template4.json', '2023-09-26 17:46:30'),
	(5, 'test5', '/static/template5.json', '2023-09-26 17:46:30'),
	(6, 'test6', '/static/template6.json', '2023-09-26 17:46:30'),
	(7, 'test7', '/static/template7.json', '2023-09-26 17:46:30'),
	(8, 'test8', '/static/template8.json', '2023-09-26 17:46:30');

-- 테이블 inventory.tb_vpc 구조 내보내기
CREATE TABLE IF NOT EXISTS `tb_vpc` (
  `vpc_id` varchar(50) NOT NULL COMMENT 'vpc id',
  `service_instance_id` varchar(32) NOT NULL COMMENT '서비스 인스턴스 id',
  `vpc_cidr` varchar(20) NOT NULL COMMENT 'vpc ip 대역',
  `subnet_cidr` varchar(20) NOT NULL COMMENT 'subnet ip 대역',
  `vpc_create_date` datetime NOT NULL COMMENT 'vpc 생성일시',
  `vpn_tunnel_ip` varchar(15) NOT NULL COMMENT 'vpn tunnel(vgw) ip',
  `vpn_tunnel_create_date` datetime NOT NULL COMMENT 'vpn tunnel(vgw) 생성일시',
  PRIMARY KEY (`vpc_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='애플리케이션 정보';

-- 테이블 데이터 inventory.tb_vpc:~0 rows (대략적) 내보내기

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
