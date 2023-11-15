package com.mcmp.orchestrator.instance.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import com.mcmp.orchestrator.dto.ServiceDTO;
import com.mcmp.orchestrator.dto.ServiceInstanceDTO;
import com.mcmp.orchestrator.dto.TemplateDTO;
import com.mcmp.orchestrator.entity.ApplicationEntity;
import com.mcmp.orchestrator.entity.ServiceInfoEntity;
import com.mcmp.orchestrator.entity.ServiceInstanceEntity;
import com.mcmp.orchestrator.entity.TemplateEntity;
import com.mcmp.orchestrator.entity.VpcEntity;
import com.mcmp.orchestrator.exception.InvalidDataException;
import com.mcmp.orchestrator.exception.InvalidInputException;
import com.mcmp.orchestrator.repository.AppRepository;
import com.mcmp.orchestrator.repository.ServiceInfoRepository;
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
 * SA Orchestrator 서비스/어플리케이션 서비스 클래스
 * 
 * @details SA Orchestrator 서비스/어플리케이션 서비스 클래스
 * @author 오승재
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstanceService {

	private final TemplateParser parser;
	private final AirflowTriggerAPI triggerAPI;
	private final ServiceInfoRepository serviceRepository;
	private final ServiceInstanceRepository serviceInstanceRepository;
	private final AppRepository appRepository;
	private final ServiceTemplateRepository templateRepository;
	private final VpcRepository vpcRepository;

	/**
	 * 서비스 생성
	 * @param String templateId
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerInstantiation(String templateId) {
		log.info("[Service instantiation] templateId : {}", templateId);
		
		TemplateEntity templateEntity = templateRepository.findByServiceTemplateId(templateId);
		if(ObjectUtils.isEmpty(templateEntity) || !"none".equals(templateEntity.getAcrossType().toLowerCase())) {
			log.error("[Service instantiation] {} is invalid service template.", templateId);
			throw new InvalidInputException("invalid template id");
		}
		
		TemplateDTO template = JsonUtil.readJsonFiletoObject(templateEntity.getServiceTemplatePath(), TemplateDTO.class);
	    log.info("[Service instantiation] template : {}", template);
		
        if(ObjectUtils.isEmpty(template)) {
            log.error("[Service instantiation] Template {} is invalid service template or template path.", templateId);
            throw new InvalidInputException("invalid template id or template path");
        }
	    
		// 인벤토리에 insert 
		ServiceInfoEntity newService = ServiceInfoEntity.builder()
				.serviceTemplateId(template.getServiceTemplateId())
				.serviceName(template.getServiceTemplateContents())
				.serviceStatus("INIT")
				.serviceCreateDate(CommonUtil.getCurrentDate())
				.deleteYn("N")
				.build();
		serviceRepository.save(newService);
		
		// 템플릿 파일 읽기
		List<ServiceInstanceDTO> instanceList = parser.parse(template);
		for(ServiceInstanceDTO service : instanceList) {
			service.setServiceId(newService.getServiceId());
		}
		
		// Airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service_id", newService.getServiceId());
		conf.put("service_list", instanceList);
		
		// Airflow API 호출
		ServiceDTO response = triggerAPI.triggerInstantiation(conf);
		response.setServiceId(newService.getServiceId());
		response.setServiceTemplateId(newService.getServiceTemplateId());
		
		return response;
	}
	
	/**
	 * 기존의 vpc와 함께 서비스 생성
	 * @param String templateId, List<String> vpcIds
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerInstantiationWithVpc(String templateId, List<String> vpcIds) {
		
		TemplateEntity templateEntity = templateRepository.findById(templateId).get();
		// 잘못된 템플릿 ID인 경우
		if(ObjectUtils.isEmpty(templateEntity) || !"none".equals(templateEntity.getAcrossType().toLowerCase())) {
			log.error("[Service instantiation with vpc id] {} is invalid across service template.", templateId);
			throw new InvalidInputException("invalid template id");
		}
		List<VpcEntity> vpcList = vpcRepository.findAllByVpcId(vpcIds);
		// 잘못된 VPC ID인 경우
		if(ObjectUtils.isEmpty(vpcList)) {
			log.error("[Service instantiation with vpc Id] {} are invalid vpc ids.", vpcIds);
			throw new InvalidInputException("invalid vpc id");
		}
		// 선택된 템플릿의 타겟 CSP들과 같은지 체크하기 위한 valid list
		List<VpcEntity> validVpcList = vpcList.stream().filter(a -> a.getCsp().toUpperCase().equals(templateEntity.getTargetCsp1().toUpperCase())
				||a.getCsp().toUpperCase().equals(templateEntity.getTargetCsp2().toUpperCase())).toList();
		// 받은 VPC ID중 잘못된 ID가 있는 경우
		if(vpcIds.size() != validVpcList.size()) {
			List<String> invalidIds = vpcIds.stream().filter(a -> !validVpcList.stream().map(b -> b.getVpcId()).toList().contains(a)).toList();
			log.error("[Service instantiation with vpc id] {} is invalid vpc id.", invalidIds);
			throw new InvalidInputException("invalid vpc id");
		} 
		int count = serviceInstanceRepository.getCountById(vpcList.stream().map(vpc -> vpc.getServiceInstanceId()).toList());
		// 해당 VPC가 이미 사용되고 있는 경우
		if(count > 0) {
			log.error("[Service instantiation with vpc Id] Vpc id {} is already in use.", vpcIds);
			throw new InvalidInputException("Vpc is already in use");
		}
		
		TemplateDTO template = JsonUtil.readJsonFiletoObject(templateEntity.getServiceTemplatePath(), TemplateDTO.class);
	    log.info("[Service instantiation with vpc Id] template : {}, vpcList : {}", template, vpcList);
	    
        if(ObjectUtils.isEmpty(template)) {
            log.error("[Service instantiation with vpc Id] Template {} is invalid service template or template path.", templateId);
            throw new InvalidInputException("invalid template id or template path");
        }
        
		// 인벤토리에 insert 
		ServiceInfoEntity newService = ServiceInfoEntity.builder()
				.serviceTemplateId(templateEntity.getServiceTemplateId())
				.serviceName(template.getServiceTemplateContents())
				.serviceStatus("INIT")
				.serviceCreateDate(CommonUtil.getCurrentDate())
				.deleteYn("N")
				.build();
		serviceRepository.save(newService);
		
		// 템플릿 파일 읽기
		List<ServiceInstanceDTO> instanceList = parser.parse(template, vpcList);
		for(ServiceInstanceDTO service : instanceList) {
			service.setServiceId(newService.getServiceId());
		}
		
		// Airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service_id", newService.getServiceId());
		conf.put("service_list", instanceList);
		
		// Airflow API 호출
		ServiceDTO response = triggerAPI.triggerInstantiation(conf);
		response.setServiceId(newService.getServiceId());
		response.setServiceTemplateId(newService.getServiceTemplateId());
		
		return response;
	}

	/**
	 * 서비스 삭제
	 * @param List<String> serviceIdList
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerTermination(List<String> serviceIdList) {
		log.info("[Service termination] serviceIdList : {}", serviceIdList);
		ServiceDTO response = new ServiceDTO();
		
		// 서비스 아이디로 서비스 조회
		List<ServiceInfoEntity> serviceInfoList = serviceRepository.findAllByServiceIdList(serviceIdList);
		// 잘못된 서비스 ID인 경우
		if(ObjectUtils.isEmpty(serviceInfoList)) {
			log.error("[Service termination] {} are invalid service ids.", serviceIdList);
			throw new InvalidInputException("Invalid service id");
		} else if(serviceIdList.size() != serviceInfoList.size()) {
			List<String> invalids = serviceInfoList.stream().filter(a -> serviceIdList.contains(a.getServiceId())).map(a -> a.getServiceId()).toList();
			log.error("[Service termination] {} are invalid service ids.", invalids);
			throw new InvalidInputException("There is invalid service id in service id list.");
		}
		
		// 받은 서비스 아이디로 deleteYn 값 Y로 업데이트
		for(ServiceInfoEntity serviceInfo : serviceInfoList) {
			serviceInfo.setDeleteYn("Y");
			serviceRepository.save(serviceInfo);
		}
		
		// 받은 서비스 아이디 리스트로 서비스 인스턴스 목록 조회
		List<ServiceInstanceEntity> serviceList = serviceInstanceRepository.findAllByServiceId(serviceIdList);
		if(ObjectUtils.isEmpty(serviceList)) {// TODO - 이부분에서 부분적으로만 null 인 경우의 수도 생각해서 없는 것들 삭제해야될 듯
			serviceRepository.deleteAll(serviceInfoList);
			log.warn("[Service termination] No service instance exists in service ids {}.", serviceIdList);
			log.warn("[Service termination] The given service will be deleted...");
			response.setServiceId(serviceIdList.toString()); 
			response.setServiceStatus("canceled");
			return response;
		} else if(serviceList.size() != serviceIdList.size()) {
			List<String> validIds = serviceList.stream().map(a -> a.getServiceId()).toList();
			for(ServiceInfoEntity info : serviceInfoList) {
				if(validIds.contains(info.getServiceId())) {
					continue;
				} else {
					log.warn("[Service termination] No service instance exists in service id {}.", info.getServiceId());
					serviceRepository.delete(info);
				}
			}
		}
		
		// entity를 dto로 변환
		List<ServiceInstanceDTO> instanceList = ServiceInstanceDTO.listOf(serviceList);
		
		// 해당 서비스 인스턴스에 해당하는 어플리케이션 정보 인벤토리에서 삭제
		appRepository.deleteAllByServiceInstanceIdList(serviceList.stream().map(a -> a.getServiceInstanceId()).toList());
		
		// Airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service_list", instanceList);
		
		response = triggerAPI.triggerTermination(conf);
		response.setServiceId(serviceIdList.toString());
		return response;
	}
	
	/**
	 * 서비스 헬스 체크
	 * @param String serviceId
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerHealthCheck(String serviceId) {
		
		ServiceInfoEntity serviceInfo = serviceRepository.findByServiceId(serviceId);
		
		// 잘못된 서비스 ID인 경우
		if(ObjectUtils.isEmpty(serviceInfo)) {
			log.warn("[Service health check] service id {} is invalid.", serviceId);
			throw new InvalidInputException("Invalid service id");
		} else if(serviceInfo.getServiceStatus().indexOf("FAIL") >= 0) {
			log.warn("[Service health check] service id {} instantiation failed.", serviceId);
		}
		
		// 받은 서비스 아이디 리스트로 서비스 인스턴스 목록 조회
		List<ServiceInstanceEntity> serviceList = serviceInstanceRepository.findAllByServiceId(serviceId);
		if(ObjectUtils.isEmpty(serviceList)) {
			log.error("[Service health check] No service instance exists in service id {}.", serviceId);
			throw new InvalidDataException("Something went wrong. No service instance exists...");
		}
		
		// entity를 dto로 변환
		List<ServiceInstanceDTO> instanceList = ServiceInstanceDTO.listOf(serviceList);
		
		ApplicationEntity appEntity = null;
		for(ServiceInstanceDTO service : instanceList) {
			appEntity = appRepository.findOneByServiceInstanceId(service.getServiceInstanceId());
			if(!ObjectUtils.isEmpty(appEntity)) {
				service.setApplicationId(String.valueOf(appEntity.getApplicationId()));
				service.setApplicationType(appEntity.getApplicationType());
			} else if(serviceInfo.getServiceStatus().indexOf("FAIL") >= 0) {
				serviceInfo.setServiceStatus("ERROR");
				serviceRepository.save(serviceInfo);
				log.error("[Service health check] Service id {} instantiation seems to fail...", serviceId);
				throw new InvalidDataException("Service instantiation failed...");
			} else {
				log.error("[Service health check] No app exists in service id {}", serviceId);
				throw new InvalidDataException("Something went wrong. No app exists...");
			}
		}
		
		// Airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service_list", instanceList);
		
		ServiceDTO response = triggerAPI.triggerHealthCheck(conf);
		response.setServiceId(serviceId);
		return response;
	}

	/**
	 * 모니터링 agent 배포
	 * @param String serviceId
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerAgentDeploy(String serviceId) {
		
		ServiceInfoEntity serviceInfo = serviceRepository.findByServiceId(serviceId);
		// 잘못된 서비스 ID인 경우
		if(ObjectUtils.isEmpty(serviceInfo)) {
			log.error("[Deploy monitoring agent] Service id {} is invalid.", serviceId);
			throw new InvalidInputException("Invalid service id");
		}
		
		// 받은 서비스 아이디로 서비스 인스턴스 목록 조회
		List<ServiceInstanceEntity> serviceList = serviceInstanceRepository.findAllForServiceAgent(serviceId);
		// 해당 서비스에 서비스 인스턴스가 없는 경우
		if(ObjectUtils.isEmpty(serviceList)) {
			log.error("[Deploy monitoring agent] No service instance exists in service id {}.", serviceId);
			throw new InvalidDataException("Something went wrong. No service instance exists...");
		}
		// 해당 서비스에 모니터링 agent가 이미 배포된 경우
		for(ServiceInstanceEntity serviceInstanceEntity : serviceList) {
			if(ObjectUtils.isEmpty(serviceInstanceEntity.getAgentDeployYn())) break;
			if("Y".equals(serviceInstanceEntity.getAgentDeployYn().toUpperCase())) {
				log.error("[Deploy monitoring agent] Monitoring agents are already deployed to service id {}.", serviceId);
				throw new InvalidInputException("Monitoring agents are already deployed.");
			}
		}
		
		// entity를 dto로 변환
		List<ServiceInstanceDTO> instanceList = ServiceInstanceDTO.listOf(serviceList);
		
		// Airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service_List", instanceList);
		
		ServiceDTO response = triggerAPI.triggerAgentDeploy(conf);
		response.setServiceId(serviceId);
		return response;
	}
	
	/**
	 * 서비스 인스턴스 기준으로 모니터링 agent 배포
	 * @param String serviceInstanceId
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerAgentDeployToInstance(String serviceInstanceId) {
		
		// 받은 서비스 인스턴스 아이디로 DB 조회
		ServiceInstanceEntity service = serviceInstanceRepository.findOneByServiceInstanceId(serviceInstanceId);
		// 해당 서비스에 서비스 인스턴스가 없는 경우
		if(ObjectUtils.isEmpty(service)) {
			log.error("[Deploy monitoring agent to instance] Service instance id {} is invalid.", serviceInstanceId);
			throw new InvalidInputException("Invalid service instance id");
		}
		// 해당 서비스에 모니터링 agent가 이미 배포된 경우
		if("Y".equals(service.getAgentDeployYn().toUpperCase())) {
			log.error("[Deploy monitoring agent to instance] Monitoring agent is already deployed to service instance id {}.", serviceInstanceId);
			throw new InvalidInputException("Monitoring agent is already deployed.");
		}
		
		// entity를 dto로 변환, 기존의 dag 그대로 사용하기 위해 list에 넣어줌
		List<ServiceInstanceDTO> instanceList = List.of(ServiceInstanceDTO.of(service));
		
		// Airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service_List", instanceList);
		
		ServiceDTO response = triggerAPI.triggerAgentDeploy(conf);
		if(ObjectUtils.isEmpty(service.getServiceId())) {
			response.setAcrossServiceId(service.getAcrossServiceId());
		} else {
			response.setServiceId(service.getServiceId());
		}
		return response;
	}

	/**
	 * 서비스 마이그레이션
	 * @param String serviceId, String templateId
	 * @return ServiceDTO
	 */
	public ServiceDTO migrateService(String serviceId, String templateId) {
		log.info("[Service migration] serviceId : {}, templateId : {}", serviceId, templateId);
		
		// 템플릿 아이디로 템플릿 경로 조회
		TemplateEntity templateEntity = templateRepository.findByServiceTemplateId(templateId);
		// 잘못된 템플릿 아이디일 경우
		if(ObjectUtils.isEmpty(templateEntity) || !"none".equals(templateEntity.getAcrossType().toLowerCase())) {
			log.error("[Service migration] Template id {} is invalid.", templateId);
			throw new InvalidInputException("Invalid template Id");
		}
		
		// 서비스 인포테이블 마이그레이션 업데이트
		ServiceInfoEntity serviceInfoEntity = serviceRepository.findByServiceId(serviceId);
		// 잘못된 서비스 아이디일 경우
		if(ObjectUtils.isEmpty(serviceInfoEntity)) {
			log.error("[Service migration] Service id {} is invalid.", serviceId);
			throw new InvalidInputException("Invalid service Id");
		} else if(serviceInfoEntity.getServiceTemplateId().equals(templateEntity.getServiceTemplateId())) {
			log.error("[Service migration] Can not migrate to the same csp {}", templateEntity.getTargetCsp1());
			throw new InvalidInputException("Service cannot migrate to the same cloud service provider.");
		}
		String originalTemplate = serviceInfoEntity.getServiceTemplateId();
		serviceInfoEntity.setServiceTemplateId(templateId);
		serviceInfoEntity.setServiceStatus("MIGRATING");
		serviceRepository.save(serviceInfoEntity);
		
		TemplateDTO template = JsonUtil.readJsonFiletoObject(templateEntity.getServiceTemplatePath(), TemplateDTO.class);
	    log.info("[Service migration] template : {}", template);
		
		// 새 서비스 인포 생성 
//		ServiceInfoEntity newService = ServiceInfoEntity.builder()
//				.serviceTemplateId(templateEntity.getServiceTemplateId())
//				.serviceName(template.getServiceTemplateContents())
//				.serviceStatus("MIGRATING")
//				.serviceCreateDate(CommonUtil.getCurrentDate())
//				.deleteYn("N")
//				.build();
		// 지워야될 수도 있음

        if(ObjectUtils.isEmpty(template)) {
            log.error("[Service migration] Template {} is invalid service template or template path.", templateId);
            throw new InvalidInputException("invalid template id or template path");
        }
        
		// 서비스 인스턴스 아이디로 서비스 인스턴스 조회
		ServiceInstanceEntity serviceInstance = serviceInstanceRepository.findOneByServiceId(serviceId);
		// 해당 서비스에 속한 VM 인스턴스가 없는 경우
		if(ObjectUtils.isEmpty(serviceInstance)) {
			serviceInfoEntity.setServiceTemplateId(originalTemplate);
			serviceInfoEntity.setServiceStatus("ERROR");
			serviceRepository.save(serviceInfoEntity);
			throw new InvalidDataException("Something went wrong. No service instance exists...");
		}
		
		// 템플릿 파일 읽기
		ServiceInstanceDTO service = parser.parseForMigration(template);
		// 서비스 인스턴스 아이디로 기존의 어플리케이션 조회
		ApplicationEntity app = appRepository.findOneByServiceInstanceId(serviceInstance.getServiceInstanceId());
		// 해당 서비스에 속한 APP이 없는 경우
		if(ObjectUtils.isEmpty(app)) {
			serviceInfoEntity.setServiceTemplateId(originalTemplate);
			serviceInfoEntity.setServiceStatus("ERROR");
			serviceRepository.save(serviceInfoEntity);
			log.warn("[Service migration] Something went wrong. No app exists...");
			throw new InvalidDataException("Something went wrong. No app exists...");
		}
		service.setServiceId(serviceId);
		service.setApplicationId(app.getApplicationId());
		service.setApplicationType(app.getApplicationType());
		service.setPublicIp(serviceInstance.getVmPublicIp());
		
		// Airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service", service);
		conf.put("original_service_instance_id", serviceInstance.getServiceInstanceId());
		conf.put("service_id", serviceId);
		
		ServiceDTO response = triggerAPI.triggerServiceMigration(conf);
		response.setServiceId(serviceId);
		return response;
	}
	
	/**
	 * 서비스 마이그레이션 시 기존의 서비스 인스턴스 및 어플리케이션 삭제
	 * @param String serviceId, ServiceInstanceDTO service
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerTerminationForMigration(String serviceId, ServiceInstanceDTO service) {
		log.info("[Service migration - delete orignal data] serviceId : {}", serviceId);
		
		// 해당 서비스 인스턴스에 해당하는 어플리케이션 정보 인벤토리에서 삭제
		try {
			appRepository.deleteById(service.getApplicationId());
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		ServiceInstanceDTO serviceDTO = ServiceInstanceDTO.of(serviceInstanceRepository.findById(service.getServiceInstanceId()).get());
		List<ServiceInstanceDTO> serviceList = new ArrayList<>();
		serviceList.add(serviceDTO);
		
		// Airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		conf.put("service_list", serviceList);
		conf.put("is_migrating", "Y");
		
		ServiceDTO response = triggerAPI.triggerTermination(conf);
		response.setServiceId(serviceId);
		return response;
	}
	
	/**
	 * 서비스/연계 서비스/스케쥴링 헬스체크
	 * @return ServiceDTO
	 */
	public ServiceDTO triggerScheduledHealthCheck(String flag) {
		
		// 모든 서비스 인스턴스 조회
		List<ServiceInstanceEntity> serviceList = serviceInstanceRepository.findAllForHealthCheck();
		String tag = "";
		ServiceDTO response = new ServiceDTO();
		switch(flag) {
			case "SERVICE": 
				serviceList = serviceList.stream().filter(a -> ObjectUtils.isEmpty(a.getAcrossServiceId()) 
						|| "0".equals(a.getAcrossServiceId())).toList();
				tag = "[Services health check]";
				break;
			case "ACROSS": 
				serviceList = serviceList.stream().filter(a -> ObjectUtils.isEmpty(a.getServiceId())).toList();
				tag = "[Across services health check]";
				break;
			case "ALL":
			default: 
				tag = "[Scheduled health check]";
				break;
		}
		if(ObjectUtils.isEmpty(serviceList) && !"ALL".equals(flag)) {
			log.info("{} There is no service to health check.", tag);
			return null;
		} else if(ObjectUtils.isEmpty(serviceList) && "ALL".equals(flag)) {
			log.info("{} There is no service to health check.", tag);
			return null;
		} else {
			log.info("{} serviceList : {}", tag, serviceList);
		}
		
		// entity를 dto로 변환
		List<ServiceInstanceDTO> instanceList = ServiceInstanceDTO.listOf(serviceList);
		List<ApplicationEntity> appList = appRepository.findAllByServiceInstanceId(instanceList.stream().map(a -> a.getServiceInstanceId()).toList());
		ApplicationEntity appEntity = null;
		if(!ObjectUtils.isEmpty(appList)) {
			for(ServiceInstanceDTO service : instanceList) {
				// 서비스 인스턴스 DTO에 어플리케이션 정보 조회 후 넣어줌
				appEntity = appList.stream().filter(a -> a.getServiceInstanceId().equals(service.getServiceInstanceId())).findFirst().orElse(null);
				if(!ObjectUtils.isEmpty(appEntity)) {
					service.setApplicationId(String.valueOf(appEntity.getApplicationId()));
					service.setApplicationType(appEntity.getApplicationType());
				} else {
					log.warn("{} Service instance id {} seems to have no apps deployed...", tag, service.getServiceInstanceId());
				}
			}
		} else {
			log.warn("{} service instance list {} seems to have no apps deployed...", tag, instanceList);
		}
		
		// Airflow request body 생성
		Map<String, Object> conf = new HashMap<>();
		if(!ObjectUtils.isEmpty(serviceList)) {
			conf.put("service_list", instanceList);
			log.info("{} {} service instances will be checked.", tag, serviceList.size());
			response = triggerAPI.triggerHealthCheck(conf);
			if("SERVICE".equals(flag)) response.setServiceId(serviceList.stream().map(a -> a.getServiceId()).toList().toString());
			if("ACROSS".equals(flag)) response.setAcrossServiceId(serviceList.stream().map(a -> a.getAcrossServiceId()).distinct().toList().toString());
		} else {
			log.info("{} There is no service to health check.", tag);
		}
		
		return response;
	}

	/**
	 * 서비스 생성 에러 체크
	 */
	public void triggerServiceFailureCheck() {
		
		// 모든 서비스 조회
		List<ServiceInfoEntity> serviceList = serviceRepository.findAll();
		int count = 0;
		for (ServiceInfoEntity service : serviceList) {
			if(service.getServiceStatus().contains("FAIL")) {
				count++;
				service.setServiceStatus("ERROR");
			}
		}
		serviceRepository.saveAll(serviceList);
		log.info("[Scheduled service failure check] found {} services in failure status...", count);
	}
}
