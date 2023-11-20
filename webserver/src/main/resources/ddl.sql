-- MariaDB dump 10.19  Distrib 10.11.5-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: inventory
-- ------------------------------------------------------
-- Server version	10.11.5-MariaDB-1:10.11.5+maria~ubu2204

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+09:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tb_across_service`
--

DROP TABLE IF EXISTS `tb_across_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_across_service` (
  `across_service_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '연계 서비스 id',
  `across_service_name` varchar(50) NOT NULL COMMENT '연계 서비스명',
  `across_type` varchar(12) NOT NULL COMMENT '연계 타입 (VPN/GSLB/ETC)',
  `across_status` varchar(100) NOT NULL COMMENT '연계 서비스 상태',
  `gslb_domain` varchar(100) DEFAULT NULL COMMENT 'GSLB 도메인',
  `gslb_csp` varchar(10) DEFAULT NULL COMMENT '메인 GSLB의 CSP',
  `gslb_weight` smallint(6) DEFAULT NULL COMMENT 'GSLB 가중치',
  `customer_gslb_weight` smallint(6) DEFAULT NULL COMMENT '타겟 GSLB 가중치',
  `delete_yn` char(1) NOT NULL DEFAULT 'N' COMMENT '삭제 여부',
  `across_create_date` datetime NOT NULL COMMENT '연계 서비스 생성일시',
  PRIMARY KEY (`across_service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='연계 서비스 정보';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_across_service`
--

LOCK TABLES `tb_across_service` WRITE;
/*!40000 ALTER TABLE `tb_across_service` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_across_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_application`
--

DROP TABLE IF EXISTS `tb_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_application` (
  `application_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '애플리케이션 id',
  `service_instance_id` varchar(50) NOT NULL COMMENT '서비스 인스턴스 id',
  `application_name` varchar(100) NOT NULL COMMENT '애플리케이션명',
  `application_type` varchar(36) NOT NULL COMMENT '애플리케이션 타입',
  `application_activate_yn` char(1) NOT NULL DEFAULT 'N' COMMENT '애플리케이션 활성화 여부(Y/N)',
  `application_create_date` datetime NOT NULL COMMENT '애플리케이션 생성일시',
  PRIMARY KEY (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='애플리케이션 정보';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_application`
--

LOCK TABLES `tb_application` WRITE;
/*!40000 ALTER TABLE `tb_application` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_csp_credential`
--

DROP TABLE IF EXISTS `tb_csp_credential`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_csp_credential` (
  `csp_credential_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'CSP credential id',
  `csp` varchar(5) NOT NULL COMMENT '클라우드 서비스 프로바이더 타입',
  `credentials` text NOT NULL COMMENT 'credential 정보(JSON)',
  PRIMARY KEY (`csp_credential_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='CSP credential 정보';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_csp_credential`
--

LOCK TABLES `tb_csp_credential` WRITE;
/*!40000 ALTER TABLE `tb_csp_credential` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_csp_credential` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_migration`
--

DROP TABLE IF EXISTS `tb_migration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_migration` (
  `migration_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '마이그레이션 id',
  `service_id` bigint(20) NOT NULL COMMENT '서비스 id',
  `db_dump_file_path` varchar(100) DEFAULT NULL COMMENT 'db 백업 파일 경로',
  PRIMARY KEY (`migration_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='마이그레이션 정보';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_migration`
--

LOCK TABLES `tb_migration` WRITE;
/*!40000 ALTER TABLE `tb_migration` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_migration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_service_info`
--

DROP TABLE IF EXISTS `tb_service_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_service_info` (
  `service_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '서비스 id',
  `service_name` varchar(100) DEFAULT NULL COMMENT '서비스명',
  `service_template_id` bigint(20) NOT NULL COMMENT '서비스 템플릿 id',
  `service_status` varchar(100) NOT NULL COMMENT '서비스 상태(RUNNING/WAITING/SUCCESS/FAILED/ERROR)',
  `delete_yn` char(1) NOT NULL DEFAULT 'N' COMMENT '삭제 여부',
  `service_create_date` datetime NOT NULL COMMENT '서비스 생성일시',
  PRIMARY KEY (`service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='서비스 정보';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_service_info`
--

LOCK TABLES `tb_service_info` WRITE;
/*!40000 ALTER TABLE `tb_service_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_service_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_service_instance`
--

DROP TABLE IF EXISTS `tb_service_instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_service_instance` (
  `service_instance_id` varchar(50) NOT NULL COMMENT '서비스 인스턴스 id',
  `across_service_id` bigint(20) DEFAULT NULL COMMENT '연계 서비스 id',
  `service_id` bigint(20) DEFAULT NULL COMMENT '서비스 id',
  `vpc_id` bigint(20) DEFAULT NULL COMMENT 'vpc id',
  `csp` varchar(5) NOT NULL COMMENT '클라우드 서비스 프로바이더 타입',
  `vm_instance_id` varchar(150) NOT NULL COMMENT 'vm 인스턴스 id',
  `vm_instance_name` varchar(100) NOT NULL COMMENT 'vm 인스턴스명',
  `vm_instance_status` varchar(50) NOT NULL COMMENT 'vm 인스턴스 상태(health)',
  `vm_instance_public_ip` varchar(15) DEFAULT NULL COMMENT 'vm 인스턴스 public ip',
  `vm_instance_private_ip` varchar(15) DEFAULT NULL COMMENT 'vm 인스턴스 private ip',
  `vm_instance_create_date` datetime NOT NULL COMMENT 'vm 인스턴스 생성일시',
  `vm_memory_type` varchar(50) DEFAULT NULL COMMENT 'vm memory type',
  `agent_activate_yn` char(1) NOT NULL DEFAULT 'N' COMMENT 'agent 활성화 여부',
  `agent_deploy_yn` char(1) NOT NULL DEFAULT 'N' COMMENT 'agent 배포 여부(Y/N)',
  `agent_deploy_date` datetime DEFAULT NULL COMMENT 'agent 배포 일시',
  `tfstate_file_path` varchar(100) NOT NULL COMMENT '서비스 템플릿 파일 경로',
  `gcp_healthcheck_flag` varchar(50) DEFAULT NULL COMMENT 'gcp healthcheck 플래그(gcp project id)',
  PRIMARY KEY (`service_instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='서비스 인스턴스 정보';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_service_instance`
--

LOCK TABLES `tb_service_instance` WRITE;
/*!40000 ALTER TABLE `tb_service_instance` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_service_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_service_template`
--

DROP TABLE IF EXISTS `tb_service_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_service_template` (
  `service_template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '서비스 템플릿 id',
  `service_template_name` varchar(50) NOT NULL COMMENT '서비스 템플릿명',
  `across_type` varchar(12) DEFAULT NULL COMMENT '연계 타입 (VPN/GSLB/ETC)',
  `target_csp1` varchar(5) NOT NULL COMMENT '타겟 클라우드 서비스 프로바이더 타입1',
  `target_csp2` varchar(5) NOT NULL COMMENT '타겟 클라우드 서비스 프로바이더 타입2',
  `service_template_path` varchar(100) NOT NULL COMMENT '서비스 템플릿 파일 경로',
  `service_template_create_date` datetime NOT NULL COMMENT '서비스 템플릿 생성일시',
  PRIMARY KEY (`service_template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='서비스 템플릿 정보';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_service_template`
--

LOCK TABLES `tb_service_template` WRITE;
/*!40000 ALTER TABLE `tb_service_template` DISABLE KEYS */;
INSERT INTO `tb_service_template` VALUES
(1,'test1','NONE','AWS','','/static/template.json','2023-09-26 17:46:11'),
(2,'test2','NONE','GCP','','/static/template2.json','2023-09-26 17:46:11'),
(3,'test3','NONE','AZURE','','/static/template3.json','2023-09-26 17:46:11'),
(4,'test4','VPN_TUNNEL','AWS','GCP','/static/template4.json','2023-09-26 17:46:30'),
(5,'test5','GSLB','AWS','AZURE','/static/template5.json','2023-09-26 17:46:30'),
(6,'test6','VPN_TUNNEL','GCP','AZURE','/static/template6.json','2023-09-26 17:46:30'),
(7,'test7','VPN_TUNNEL','AWS','AZURE','/static/template7.json','2023-09-26 17:46:30'),
(8,'test8','GSLB','GCP','AZURE','/static/template8.json','2023-09-26 17:46:30');
/*!40000 ALTER TABLE `tb_service_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_vpc`
--

DROP TABLE IF EXISTS `tb_vpc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_vpc` (
  `vpc_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'vpc id',
  `service_instance_id` varchar(50) NOT NULL COMMENT '서비스 인스턴스 id',
  `csp` varchar(5) NOT NULL COMMENT '클라우드 서비스 프로바이더 타입',
  `vpc_cidr` varchar(20) NOT NULL COMMENT 'vpc ip 대역',
  `subnet_cidr` varchar(20) NOT NULL COMMENT 'subnet ip 대역',
  `vpc_create_date` datetime NOT NULL COMMENT 'vpc 생성일시',
  `vpn_tunnel_ip` varchar(15) NOT NULL COMMENT 'vpn tunnel(vgw) ip',
  `vpn_tunnel_create_date` datetime NOT NULL COMMENT 'vpn tunnel(vgw) 생성일시',
  PRIMARY KEY (`vpc_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='VPC 정보';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_vpc`
--

LOCK TABLES `tb_vpc` WRITE;
/*!40000 ALTER TABLE `tb_vpc` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_vpc` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
