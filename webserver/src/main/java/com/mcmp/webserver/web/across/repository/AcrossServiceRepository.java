package com.mcmp.webserver.web.across.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mcmp.webserver.web.across.dto.IntegratedAcrossServiceListVO;
import com.mcmp.webserver.web.across.dto.IntegratedAcrossServiceVO;
import com.mcmp.webserver.web.across.entity.AcrossService;

@Repository
public interface AcrossServiceRepository extends JpaRepository<AcrossService, Long> {
	
	//연계 서비스 통합 목록 조회
	@Query(
			nativeQuery = true,
			value = """
					SELECT  
							AC.across_service_id AS acrossServiceId,
							AC.across_service_name AS acrossServiceName,
							AC.across_type AS acrossType,
							AC.across_status AS acrossStatus,
							AC.gslb_domain AS gslbDomain,
							AC.gslb_csp AS gslbCsp,
							AC.gslb_weight AS gslbWeight,
							AC.customer_gslb_weight AS customerGslbWeight,
							AC.delete_yn AS deleteYn,
							AC.across_create_date AS acrossCreateDate,
							INS.service_instance_id AS serviceInstanceId
					  FROM  tb_across_service AC
					  LEFT  JOIN (
					 		SELECT  
						 			GROUP_CONCAT(service_instance_id SEPARATOR ',') AS service_instance_id,
									across_service_id
						  	  FROM  tb_service_instance
						      GROUP BY across_service_id
					  ) INS ON AC.across_service_id = INS.across_service_id
					 WHERE AC.delete_yn = 'N'
					 ORDER BY AC.across_service_id
					"""
	)
	public List<IntegratedAcrossServiceListVO> findIntegratedAcrossServiceList();

	//연계 서비스의 인스턴스 기준 전체 목록 조회 (2개 데이터)
	@Query(
			nativeQuery = true,
			value = """
					SELECT  
							AC.gslb_domain AS gslbDomain,
							AC.gslb_csp AS gslbCsp,
							AC.gslb_weight AS gslbWeight,
							AC.customer_gslb_weight AS customerGslbWeight,
							IF (AC.gslb_csp = INS.csp, 'Y', 'N') AS mainGslb,
							VPC.vpn_tunnel_ip AS vpnTunnelIp,
							VPC.vpn_tunnel_create_date AS vpnTunnelCreateDate,
							INS.service_instance_id AS serviceInstanceId ,
							AC.across_service_id AS acrossServiceId,
							AC.across_type AS acrossType, 
							INS.service_id AS serviceId,
					    	INS.vpc_id AS vpcId,
						   	INS.csp AS csp,
					    	INS.vm_instance_id AS vmInstanceId, 
							INS.vm_instance_name AS vmInstanceName, 
							INS.vm_instance_status AS vmInstanceStatus, 
							INS.vm_instance_public_ip AS vmInstancePublicIp, 
							INS.vm_instance_private_ip AS vmInstancePrivateIp, 
							INS.vm_instance_create_date  AS vmInstanceCreateDate, 
							INS.vm_memory_type AS vmMemoryType, 
							INS.agent_activate_yn AS agentActivateYn,
							INS.agent_deploy_yn AS agentDeployYn, 
							INS.agent_deploy_date AS agentDeployDate,
							INS.tfstate_file_path AS tfstateFilePath,
							INS.gcp_healthcheck_flag AS gcpHealthcheckFlag,
							APP.application_id AS applicationId,
							APP.application_name AS applicationName,
							APP.application_type AS applicationType,
							APP.application_activate_yn AS applicationActivateYn,
							APP.application_create_date AS applicationCreateDate
					  FROM tb_service_instance INS
					  LEFT JOIN tb_application APP ON INS.service_instance_id = APP.service_instance_id
					  LEFT JOIN tb_across_service AC ON AC.across_service_id = INS.across_service_id
					  LEFT JOIN tb_vpc VPC ON VPC.vpc_id = INS.vpc_id 
					 WHERE INS.across_service_id = :acrossServiceId
					 ORDER BY AC.across_service_id
					"""
	)
	public List<IntegratedAcrossServiceVO> findServiceInstanceListOfAcrossService(@Param("acrossServiceId") Long acrossServiceId);
	
	//연계 서비스 전체 목록 조회
	public List<AcrossService> findAllByDeleteYn(String deleteYn);
	
	//연계 서비스 단건 조회 
	public AcrossService findByAcrossServiceId(Long acrossServiceId);
}
