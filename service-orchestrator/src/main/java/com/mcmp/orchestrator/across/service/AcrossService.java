package com.mcmp.orchestrator.across.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import com.mcmp.orchestrator.across.repository.AcrossServiceRepository;
import com.mcmp.orchestrator.dto.ServiceDTO;
import com.mcmp.orchestrator.dto.ServiceInstanceDTO;
import com.mcmp.orchestrator.dto.TemplateDTO;
import com.mcmp.orchestrator.entity.AcrossServiceEntity;
import com.mcmp.orchestrator.entity.ApplicationEntity;
import com.mcmp.orchestrator.entity.ServiceInstanceEntity;
import com.mcmp.orchestrator.entity.TemplateEntity;
import com.mcmp.orchestrator.entity.VpcEntity;
import com.mcmp.orchestrator.exception.InvalidDataException;
import com.mcmp.orchestrator.exception.InvalidInputException;
import com.mcmp.orchestrator.repository.AppRepository;
import com.mcmp.orchestrator.repository.ServiceInstanceRepository;
import com.mcmp.orchestrator.repository.ServiceTemplateRepository;
import com.mcmp.orchestrator.repository.VpcRepository;
import com.mcmp.orchestrator.restApi.AirflowTriggerAPI;
import com.mcmp.orchestrator.util.CommonUtil;
import com.mcmp.orchestrator.util.JsonUtil;
import com.mcmp.orchestrator.util.TemplateParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AcrossService
 * 
 * @details 연계서비스 관련 api 서비스 클래스
 * @author 오승재
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcrossService {
	
	private final TemplateParser parser;
	private final AirflowTriggerAPI triggerAPI;
	private final AcrossServiceRepository acrossRepository;
	private final ServiceInstanceRepository serviceInstanceRepository;
	private final AppRepository appRepository;
	private final ServiceTemplateRepository templateRepository;
	private final VpcRepository vpcRepository;
	
	/**
	 * 연계 서비스 생성
	 * @param String templateId
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerAcrossInstantiation(String templateId) {
		log.info("[Across service instantiation] template id : {}", templateId);
		
		TemplateEntity templateEntity = templateRepository.findByServiceTemplateId(templateId);
		if(ObjectUtils.isEmpty(templateEntity) || "none".equals(templateEntity.getAcrossType().toLowerCase())) {
			log.error("{} is invalid across service template.", templateId);
			throw new InvalidInputException("invalid template id");
		}
		
		// 템플릿 파일 읽기
		TemplateDTO template = JsonUtil.readJsonFiletoObject(templateEntity.getServiceTemplatePath(), TemplateDTO.class);
		log.info("[Across service instantiation] template : {}", template);

        if(ObjectUtils.isEmpty(template)) {
            log.error("[Across service instantiation] Template {} is invalid service template or template path.", templateId);
            throw new InvalidInputException("invalid template id or template path");
        }
        
		// 인벤토리 연계서비스 테이블 insert
		AcrossServiceEntity newAcross = AcrossServiceEntity.builder()
				.acrossSeviceName(template.getServiceTemplateContents())
				.acrossStatus("INIT")
				.acrossType(template.getAcrossServiceType().toUpperCase())
				.acrossCreateDate(CommonUtil.getCurrentDate())
				.deleteYn("N")
				.build();
		acrossRepository.save(newAcross);
		
		// 템플릿 필요한 형태로 파싱
		List<ServiceInstanceDTO> instanceList = parser.parse(template);
		for(ServiceInstanceDTO service : instanceList) {
			service.setAcrossServiceId(newAcross.getAcrossServiceId());
		}
		
		// Airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service_list", instanceList);
		conf.put("across_service_id", newAcross.getAcrossServiceId());
		conf.put("across_service_type", templateEntity.getAcrossType());
		
		// Airflow API 호출
		ServiceDTO response = triggerAPI.triggerAcrossInstantiation(conf);
		response.setAcrossServiceId(String.valueOf(newAcross.getAcrossServiceId()));
		response.setServiceTemplateId(templateEntity.getServiceTemplateId());
		return response;
	}

	/**
	 * 기존의 vpc와 연계 서비스 생성
	 * @param String templateId, List<String> vpcIds
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerAcrossInstantiationWithVpc(String templateId, List<String> vpcIds) {
		log.info("[Across service instantiation with vpc id] template id : {}, vpcIds : {}", templateId, vpcIds);
		
		TemplateEntity templateEntity = templateRepository.findById(templateId).get();
		// 잘못된 템플릿 ID인 경우
		if(ObjectUtils.isEmpty(templateEntity) || "none".equals(templateEntity.getAcrossType().toLowerCase())) {
			log.error("[Across service instantiation with vpc id] {} is invalid across service template.", templateId);
			throw new InvalidInputException("invalid template id");
		}
		List<VpcEntity> vpcList = vpcRepository.findAllByVpcId(vpcIds);
		// 잘못된 VPC ID만 받은 경우
		if(ObjectUtils.isEmpty(vpcList)) {
			log.error("[Across service instantiation with vpc Id] {} are invalid vpc ids.", vpcIds);
			throw new InvalidInputException("invalid vpc id");
		}
		// 선택된 템플릿의 타겟 csp들과 같은지 체크하기 위한 valid list
		List<VpcEntity> validVpcList = vpcList.stream().filter(a -> a.getCsp().toUpperCase().equals(templateEntity.getTargetCsp1().toUpperCase())
				||a.getCsp().toUpperCase().equals(templateEntity.getTargetCsp2().toUpperCase())).toList();
		// 받은 VPC ID 중 잘못된 ID가 있는 경우
		if(vpcIds.size() != validVpcList.size()) {
			List<String> invalidIds = vpcIds.stream().filter(a -> !validVpcList.stream().map(b -> b.getVpcId()).toList().contains(a)).toList();
			log.error("[Across service instantiation with vpc id] {} is invalid vpc id.", invalidIds);
			throw new InvalidInputException("invalid vpc id");
		} 
		int count = serviceInstanceRepository.getCountById(vpcList.stream().map(vpc -> vpc.getServiceInstanceId()).toList());
		// 해당 VPC가 이미 사용되고 있는 경우
		if(count > 0) {
			log.error("[Across service instantiation with vpc Id] At least one of vpc id {} is already in use.", vpcIds);
			throw new InvalidInputException("vpc is already in use");
		}
		
		// 템플릿 파일 읽기
		TemplateDTO template = JsonUtil.readJsonFiletoObject(templateEntity.getServiceTemplatePath(), TemplateDTO.class);
		log.info("[Across service instantiation with vpc id] template : {}", template);

        if(ObjectUtils.isEmpty(template)) {
            log.error("[Across service instantiation with vpc Id] Template {} is invalid service template or template path.", templateId);
            throw new InvalidInputException("invalid template id or template path");
        }
        
		// 인벤토리 연계서비스 테이블 insert
		AcrossServiceEntity newAcross = AcrossServiceEntity.builder()
				.acrossSeviceName(template.getServiceTemplateContents())
				.acrossStatus("INIT")
				.acrossType(template.getAcrossServiceType().toUpperCase())
				.acrossCreateDate(CommonUtil.getCurrentDate())
				.deleteYn("N")
				.build();
		acrossRepository.save(newAcross);
		
		// 템플릿 필요한 형태로 파싱
		List<ServiceInstanceDTO> instanceList = parser.parse(template, vpcList);
		for(ServiceInstanceDTO service : instanceList) {
			service.setAcrossServiceId(newAcross.getAcrossServiceId());
		}
		
		// Airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service_list", instanceList);
		conf.put("across_service_id", newAcross.getAcrossServiceId());
		conf.put("across_service_type", templateEntity.getAcrossType());
		
		// Airflow API 호출
		ServiceDTO response = triggerAPI.triggerAcrossInstantiation(conf);
		response.setAcrossServiceId(String.valueOf(newAcross.getAcrossServiceId()));
		response.setServiceTemplateId(templateEntity.getServiceTemplateId());
		return response;
	}
	
	/**
	 * 연계 서비스 삭제
	 * @param String vmOnlyYn, List<String> acrossServiceIdList
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerAcrossTermination(String vmOnlyYn, List<String> acrossServiceIdList) {
		log.info("[Across service termination] vmOnlyYn : {}, acrossServiceIdList : {}", vmOnlyYn, acrossServiceIdList);
		ServiceDTO response = new ServiceDTO();
		
		if(ObjectUtils.isEmpty(vmOnlyYn) || "".equals(vmOnlyYn)) {
			log.error("[Across service termination] vmOnlyYn is null or empty string.");
			throw new InvalidInputException("You have to choose wheather to destroy all or only vm.");
		}
		List<AcrossServiceEntity> acrossList = acrossRepository.findAllByAcrossServiceIdList(acrossServiceIdList);
		// 연계 서비스 아이디가 없는 경우
		if(ObjectUtils.isEmpty(acrossList)) {
			log.error("[Across service termination] Across service id {} are invalid.", acrossServiceIdList);
			throw new InvalidInputException("Invalid across service ids.");
		}
		
		// 화면 출력용 변수 변경
		for(AcrossServiceEntity across : acrossList) {
			across.setDeleteYn("Y");
			acrossRepository.save(across);
		}
		
		List<ServiceInstanceEntity> acrossServiceList = serviceInstanceRepository.findAllByAcrossServiceId(acrossServiceIdList);
		// 연계 서비스에 해당하는 인스턴스가 없는 경우
		if(ObjectUtils.isEmpty(acrossServiceList)) {
			log.warn("[Across service termination] There are no vm instances in across service {}.", acrossServiceIdList);
			log.warn("[Across service termination] The given across services will be deleted...");
			acrossRepository.deleteAll(acrossList);
			response.setServiceId(acrossServiceIdList.toString()); 
			response.setServiceStatus("canceled");
			return response;
		}
		List<ServiceInstanceDTO> serviceList = ServiceInstanceDTO.listOf(acrossServiceList);
		// 해당 서비스 인스턴스에 해당하는 어플리케이션 정보 인벤토리에서 삭제
		try {
			appRepository.deleteAllByServiceInstanceIdList(acrossServiceList.stream().map(a -> a.getServiceInstanceId()).toList());
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		// airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service_list", serviceList);
		conf.put("vm_only_yn", vmOnlyYn);
		conf.put("service_id_list", acrossServiceIdList);
		
		response = triggerAPI.triggerAcrossTermination(conf);
		response.setAcrossServiceId(acrossServiceIdList.toString());
		return response;
	}

	/**
	 * 연계 서비스 헬스 체크
	 * @param String acrossServiceId
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerAcrossHealthCheck(String acrossServiceId) {
		
		AcrossServiceEntity across = acrossRepository.findByAcrossServiceId(acrossServiceId);
		// 연계 서비스가 없는 경우
		if(ObjectUtils.isEmpty(across)) {
			log.error("[Across service health check] across service {} seems to be missing.", acrossServiceId);
			throw new InvalidInputException("Invalid across service id.");
		}
		
		List<ServiceInstanceEntity> serviceList = serviceInstanceRepository.findAllByAcrossServiceId(acrossServiceId);
		// 서비스 인스턴스가 없는 경우
		if(ObjectUtils.isEmpty(serviceList)) {
			log.error("[Across service health check] There is no vm instances in across service id {}.", acrossServiceId);
			throw new InvalidDataException("Invalid across service id.");
		// vm 인스턴스가 1개인 경우
		} else if (serviceList.size() == 1) {
			log.error("[Across service health check] There is only one vm instance in across service id {}.", acrossServiceId);
			throw new InvalidDataException("Something went wrong. There is only on vm instance...");
		}
		List<ServiceInstanceDTO> instanceList = ServiceInstanceDTO.listOf(serviceList);
		ApplicationEntity appEntity = null;
		
		for(ServiceInstanceDTO service : instanceList) {
			appEntity = appRepository.findOneByServiceInstanceId(service.getServiceInstanceId());
			// 어플이 없는 경우
			if(ObjectUtils.isEmpty(appEntity)) {
				log.warn("[Across service health check] Service instance id {} in given across service seems to have no apps deployed", service.getServiceInstanceId());
				continue;
			} 
			service.setApplicationId(String.valueOf(appEntity.getApplicationId()));
			service.setApplicationType(appEntity.getApplicationType());
		}
		
		// airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service_list", instanceList);
		
		ServiceDTO response = triggerAPI.triggerAcrossRetreival(conf);
		response.setAcrossServiceId(acrossServiceId);
		return response;
	}
	
	/**
	 * 연계 서비스 monitoring agent 배포
	 * @param String acrossServiceId
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerAcrossAgentDeploy(String acrossServiceId) {
		
		// 연계 서비스 아이디가 없는 경우
		AcrossServiceEntity across = acrossRepository.findByAcrossServiceId(acrossServiceId);
		if(ObjectUtils.isEmpty(across)) {
			log.error("[Deploy monitoring agent to across service] {} is invald across service id.", acrossServiceId);
			throw new InvalidInputException("Invalid across service id.");
		}
		
		// 받은 서비스 아이디로 서비스 인스턴스 목록 조회
		List<ServiceInstanceEntity> serviceList = serviceInstanceRepository.findAllForAcrossAgent(acrossServiceId);
		if(ObjectUtils.isEmpty(serviceList)) {
			log.error("[Deploy monitoring agent to across service] across service {} has no service instances.", acrossServiceId);
			throw new InvalidDataException("Invalid across service id.");
		}
		// 해당 서비스에 모니터링 agent가 이미 배포된 경우
		for(ServiceInstanceEntity serviceInstanceEntity : serviceList) {
			if(ObjectUtils.isEmpty(serviceInstanceEntity.getAgentDeployYn())) continue;
			if("Y".equals(serviceInstanceEntity.getAgentDeployYn().toUpperCase())) {
				log.error("[Deploy monitoring agent to across service] Monitoring agent is already deployed to service instance {}", 
						serviceInstanceEntity.getServiceInstanceId());
				throw new InvalidInputException("Monitoring agent is already deployed.");
			}
		}
		
		// entity를 dto로 변환
		List<ServiceInstanceDTO> instanceList = ServiceInstanceDTO.listOf(serviceList);
		
		// airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service_List", instanceList);
		
		ServiceDTO response = triggerAPI.triggerAgentDeploy(conf);
		response.setAcrossServiceId(acrossServiceId);
		return response;
	}

	/**
	 * 연계 서비스 생성 에러 체크
	 */
	public void triggerAcrossFailureCheck() {
		
		// 모든 연계 서비스 조회
		List<AcrossServiceEntity> acrossList = acrossRepository.findAll();
		int count = 0;
		for (AcrossServiceEntity service : acrossList) {
			if(service.getAcrossStatus().contains("FAIL")) {
				count++;
				service.setAcrossStatus("ERROR");
			}
		}
		acrossRepository.saveAll(acrossList);
		log.info("[Scheduled across service failure check] found {} across services in failure status...", count);
	}
}
