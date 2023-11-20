package com.mcmp.webserver.web.serviceinfo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mcmp.webserver.web.serviceinfo.dto.IntegratedServiceListVO;
import com.mcmp.webserver.web.serviceinfo.dto.IntegratedServiceVO;
import com.mcmp.webserver.web.serviceinfo.entity.ServiceInfo;

@Repository
public interface ServiceInfoRepository extends JpaRepository<ServiceInfo, String> {
	
	//서비스 전체 목록 조회
	@Query(
		nativeQuery = true,
		value = """
				SELECT  
						ROW_NUMBER() OVER() AS rowId,
						SVC.service_id AS serviceId,
						SVC.service_name As serviceName,
						SVC.service_template_id AS serviceTemplateId,
						INS.csp AS csp,
						SVC.service_status AS serviceStatus,
						SVC.delete_yn AS deleteYn,
						SVC.service_create_date AS serviceCreateDate
				  FROM  tb_service_info SVC
			      LEFT  JOIN tb_service_instance INS
				    ON  SVC.service_id = INS.service_id
				 WHERE  SVC.delete_yn = 'N'
				 ORDER  BY SVC.service_id
				"""
	)
	public List<IntegratedServiceListVO> findIntegratedServiceList();
	
	//서비스 상세 조회
	@Query(
		nativeQuery = true,
		value = """
				SELECT  
					INS.service_id AS serviceId,
					SVC.service_name AS serviceName,
					SVC.service_template_id AS serviceTemplateId, 
					SVC.service_status AS serviceStatus,
					SVC.delete_yn AS deleteYn,
					SVC.service_create_date AS serviceCreateDate,
					INS.service_instance_id AS serviceInstanceId ,
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
			  FROM  tb_service_instance INS
			  LEFT  JOIN  tb_service_info SVC ON  SVC.service_id = INS.service_id
			  LEFT  JOIN  tb_application APP ON INS.service_instance_id = APP.service_instance_id
			 WHERE  INS.service_id = :serviceId
			 ORDER  BY INS.service_id
				"""
	)
	public IntegratedServiceVO findServiceInstanceOfService(@Param("serviceId") Long serviceId);
	
}
