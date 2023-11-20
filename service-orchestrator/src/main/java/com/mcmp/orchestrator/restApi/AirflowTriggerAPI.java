package com.mcmp.orchestrator.restApi;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mcmp.orchestrator.dto.ServiceDTO;
import com.mcmp.orchestrator.util.JsonUtil;

import lombok.RequiredArgsConstructor;

/**
 * AirflowTriggerAPI
 * 
 * @details Airflow와 rest api로 통신하기 위한 api 클래스
 * @author 오승재
 *
 */
@Component
@RequiredArgsConstructor
public class AirflowTriggerAPI {
	
	// Airflow URI - 기본 workflow trigger uri
	private final String URI_DAG_TRIGGER = "/api/v1/dags/{dag_id}/dagRuns";
	
	// Airflow DAG ID - dag id 및 각 dag를 사용하는 api
	// 서비스 생성
	private final String CREATE_DAG = "create_api_call_template_dag";
	// 서비스 삭제
	private final String DESTROY_DAG = "destroy_api_call_template_dag";
	// 서비스 헬스체크, 연계 서비스 헬스체크
	private final String RETRIEVAL_DAG = "retrieval_api_call_template_dag";
	// 연계 서비스 생성
	private final String ACROSS_CREATE_DAG = "create_across_api_call_template_dag";
	// 연계 서비스 삭제
	private final String ACROSS_DESTROY_DAG = "destroy_across_api_call_template_dag";
	// 서비스 모니터링 agent 배포, 인스턴스 모니터링 agent 배포, 연계 서비스 모니터링 agent 배포
	private final String DEPLOY_AGENT_DAG = "deploy_agent_api_call_template_dag";
	// 서비스 마이그레이션 
	private final String SERVICE_MIGRATION_DAG = "migrate_service_api_call_template_dag";
	
	private final RestTemplate restTemplate;
	
	/**
	 * 서비스 생성 dag trigger 
	 * @param Map<String, Object> conf
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerInstantiation(Map<String, Object> conf) {
		return restTemplate.postForObject(URI_DAG_TRIGGER, JsonUtil.toJson(conf), ServiceDTO.class, CREATE_DAG);
	}

	/**
	 * 서비스 삭제 dag trigger 
	 * @param Map<String, Object> conf
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerTermination(Map<String, Object> conf) {
		return restTemplate.postForObject(URI_DAG_TRIGGER, JsonUtil.toJson(conf), ServiceDTO.class, DESTROY_DAG);
	}

	/**
	 * 서비스 헬스체크 dag trigger 
	 * @param Map<String, Object> conf
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerHealthCheck(Map<String, Object> conf) {
		return restTemplate.postForObject(URI_DAG_TRIGGER, JsonUtil.toJson(conf), ServiceDTO.class, RETRIEVAL_DAG);
	}

	/**
	 * 연계 서비스 생성 dag trigger 
	 * @param Map<String, Object> conf
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerAcrossInstantiation(Map<String, Object> conf) {
		return restTemplate.postForObject(URI_DAG_TRIGGER, JsonUtil.toJson(conf), ServiceDTO.class, ACROSS_CREATE_DAG);
	}

	/**
	 * 연계 서비스 삭제 dag trigger 
	 * @param Map<String, Object> conf
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerAcrossTermination(Map<String, Object> conf) {
		return restTemplate.postForObject(URI_DAG_TRIGGER, JsonUtil.toJson(conf), ServiceDTO.class, ACROSS_DESTROY_DAG);
	}

	/**
	 * 연계 서비스 조회 dag trigger 
	 * @param Map<String, Object> conf
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerAcrossRetreival(Map<String, Object> conf) {
		return restTemplate.postForObject(URI_DAG_TRIGGER, JsonUtil.toJson(conf), ServiceDTO.class, RETRIEVAL_DAG);
	}
	
	/**
	 * 서비스 모니터링 agent 배포 dag trigger 
	 * @param Map<String, Object> conf
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerAgentDeploy(Map<String, Object> conf) {
		return restTemplate.postForObject(URI_DAG_TRIGGER, JsonUtil.toJson(conf), ServiceDTO.class, DEPLOY_AGENT_DAG);
	}

	/**
	 * 서비스 마이그레이션 dag trigger 
	 * @param Map<String, Object> conf
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerServiceMigration(Map<String, Object> conf) {
		return restTemplate.postForObject(URI_DAG_TRIGGER, JsonUtil.toJson(conf), ServiceDTO.class, SERVICE_MIGRATION_DAG);
	}

	/**
	 * 연계 서비스 모니터링 agent 배포 dag trigger 
	 * @param Map<String, Object> conf
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerAcrossAgentDeploy(Map<String, Object> conf) {
		return restTemplate.postForObject(URI_DAG_TRIGGER, JsonUtil.toJson(conf), ServiceDTO.class, DEPLOY_AGENT_DAG);
	}
}
