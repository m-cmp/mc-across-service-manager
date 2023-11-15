package com.mcmp.webserver.monitoring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mcmp.webserver.monitoring.dto.AcrossServiceStatusDTO;
import com.mcmp.webserver.monitoring.dto.AgentStatusDTO;
import com.mcmp.webserver.monitoring.dto.CSPResourceMonitoringDTO;
import com.mcmp.webserver.monitoring.dto.InstanceStatusDTO;
import com.mcmp.webserver.monitoring.dto.ServiceStatusDTO;
import com.mcmp.webserver.web.instance.entity.ServiceInstance;

public interface CSPMonitoringRepository extends JpaRepository<ServiceInstance, String> {
//	public List<CSPStatusMonitoringDTO> findAll();

	// CSP 자원별 모니터링 카운트 수
	@Query(
		nativeQuery = true,
		value = """
				SELECT 
						INS.csp AS csp,
						COUNT(DISTINCT VPC.vpc_id) AS vpcCnt,
						COUNT(DISTINCT INS.service_instance_id) AS instanceCnt,
						COUNT(DISTINCT APP.application_id) AS appCnt,
						COUNT(DISTINCT SVC.service_id) AS serviceCnt,
						COUNT(DISTINCT ACROSS.across_service_id) AS acrossServiceCnt,
						COUNT(DISTINCT ACROSS.gslb_domain) AS gslbCnt,
						COUNT(DISTINCT VPC.vpn_tunnel_ip) AS vpnCnt
				  FROM 		tb_service_instance AS INS	
				  LEFT JOIN tb_service_info AS SVC ON INS.service_id = SVC.service_id
				  LEFT JOIN tb_across_service AS ACROSS ON INS.across_service_id = ACROSS.across_service_id
				  LEFT JOIN tb_vpc AS VPC ON INS.service_instance_id = VPC.service_instance_id
				  LEFT JOIN tb_application AS APP ON INS.service_instance_id = APP.service_instance_id
				  GROUP BY INS.csp
				"""
	)
	public List<CSPResourceMonitoringDTO> findCSPStatusMonitoring();

//	-- 인스턴스
	@Query(value = "SELECT ins.vm_instance_status as status, COUNT(ins.vm_instance_status) AS cnt "
			+ "FROM tb_service_instance AS ins GROUP BY ins.vm_instance_status ", nativeQuery = true)
	public List<InstanceStatusDTO> findInstanceStatusAll();

//
//	-- 어플리케이션
//	@Query(value = "SELECT app.application_status as status, COUNT(app.application_status) AS cnt "
//			+ "FROM tb_application AS app GROUP BY app.application_status", nativeQuery = true)
//	public List<AppStatusDTO> findAppStatusAll();
	
//	-- Agent
	@Query(value = "SELECT ins.agent_activate_yn as status, COUNT(ins.agent_activate_yn) AS cnt "
			+ "FROM tb_service_instance AS ins WHERE ins.agent_deploy_yn='Y' GROUP BY ins.agent_activate_yn ", nativeQuery = true)
	public List<AgentStatusDTO> findAgentActiveYnAll();

//
//	-- 서비스
	@Query(value = "SELECT svc.service_status as status, COUNT(svc.service_status) AS cnt "
			+ "FROM tb_service_info AS svc GROUP BY svc.service_status", nativeQuery = true)
	public List<ServiceStatusDTO> findServiceStatusAll();

//
//	-- 연계서비스
	@Query(value = "SELECT across.across_status as status, COUNT(across.across_status) AS cnt "
			+ "FROM tb_across_service AS across GROUP BY across.across_status ", nativeQuery = true)
	public List<AcrossServiceStatusDTO> findAcrossServicesStatusAll();
}
