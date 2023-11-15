package com.mcmp.orchestrator.info.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import com.mcmp.orchestrator.across.repository.AcrossServiceRepository;
import com.mcmp.orchestrator.dto.ServiceDTO;
import com.mcmp.orchestrator.dto.ServiceInstanceDTO;
import com.mcmp.orchestrator.entity.AcrossServiceEntity;
import com.mcmp.orchestrator.entity.ServiceInfoEntity;
import com.mcmp.orchestrator.exception.InvalidInputException;
import com.mcmp.orchestrator.repository.ServiceInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * InfoService
 * 
 * @details 서비스 인스턴스 정보 관리를 위한 서비스 클래스
 * @author 오승재
 *
 */
@Slf4j
@Service 
@RequiredArgsConstructor
public class InfoService {
	
	private final ServiceInfoRepository serviceInfoRepository;
	private final AcrossServiceRepository acrossServiceRepository;
	// 정상 상태값 // , "STOPPED"
	private final List<String> OK_STATUS_LIST = Arrays.asList("RUNNING", "STARTING", "DEPLOY", "STANDBY", "Y");
	
	/**
	 * 서비스 인포 테이블 진행상황 업데이트
	 * @param ServiceDTO serviceDTO
	 * @return ServiceInfoEntity
	 */
	public ServiceInfoEntity updateWorkflowState(ServiceDTO serviceDTO) {
		
		// 서비스 인포 테이블 업데이트
		ServiceInfoEntity serviceInfo = serviceInfoRepository.findByServiceId(serviceDTO.getServiceId());
		if(ObjectUtils.isEmpty(serviceInfo)) {
			throw new InvalidInputException();
		}
		if(serviceInfo.getServiceStatus().contains("_FAIL") && serviceDTO.getServiceStatus().contains("_FAIL")) {
			log.info("[Workflow update] vm init failed");
			return null;
		}
		serviceInfo.setServiceStatus(serviceDTO.getServiceStatus());
		serviceInfoRepository.save(serviceInfo);
		
		return serviceInfo;
	}
	
	/**
	 * 서비스 인포 테이블 헬스체크 업데이트
	 * @param List<ServiceInstanceDTO> statusList
	 * @return Object
	 */
	public Object updateServiceStatus(List<ServiceInstanceDTO> statusList) {
		
		ServiceInfoEntity serviceInfo = null;
		AcrossServiceEntity acrossInfo = null;
		
		try {
			for(ServiceInstanceDTO service : statusList) {
				// 서비스 인포 테이블 업데이트
				String originalStatus;
				if(!ObjectUtils.isEmpty(service.getServiceId())) {
					serviceInfo = serviceInfoRepository.findById(service.getServiceId()).get();
					originalStatus = serviceInfo.getServiceStatus().toUpperCase();
					// VM 상태 체크 후 업데이트
					if(!ObjectUtils.isEmpty(service.getVmInstanceStatus()) && !"".equals(service.getVmInstanceStatus())) {
						log.info("[Service update] service instance id : {}, vm status : {}", service.getServiceInstanceId(), service.getVmInstanceStatus());
						serviceInfo.setServiceStatus(OK_STATUS_LIST.contains(service.getVmInstanceStatus().toUpperCase())? "RUNNING":"ERROR");
					// APP 상태 체크 후 업데이트
					} else if(!ObjectUtils.isEmpty(service.getApplicationStatus()) && !"".equals(service.getApplicationStatus())) {
						log.info("[Service update] service instance id : {}, app status : {}", service.getServiceInstanceId(), service.getApplicationStatus());
						serviceInfo.setServiceStatus(OK_STATUS_LIST.contains(service.getApplicationStatus().toUpperCase())? "RUNNING":"ERROR");
					}
					// 기존과 같은 상태인지 체크 후, 다른 경우에만 DB 업데이트
					if(!originalStatus.equals(serviceInfo.getServiceStatus().toUpperCase())) serviceInfoRepository.save(serviceInfo);
				// 연계 서비스 테이블 업데이트
				} else if(!ObjectUtils.isEmpty(service.getAcrossServiceId()) && !"0".equals(service.getAcrossServiceId())) {
					acrossInfo = acrossServiceRepository.findById(service.getAcrossServiceId()).get();
					originalStatus = acrossInfo.getAcrossStatus();
					// VM 상태 체크 후 업데이트
					if(!ObjectUtils.isEmpty(service.getVmInstanceStatus()) && !"".equals(service.getVmInstanceStatus())) {
						log.info("[Across service update] service instance id : {}, vm status : {}", service.getServiceInstanceId(), service.getVmInstanceStatus());
						acrossInfo.setAcrossStatus(OK_STATUS_LIST.contains(service.getVmInstanceStatus().toUpperCase())? "RUNNING":"ERROR");
					// APP 상태 체크 후 업데이트
					} else if(!ObjectUtils.isEmpty(service.getApplicationStatus()) && !"".equals(service.getApplicationStatus())) {
						log.info("[Across service update] service instance id : {}, app status : {}", service.getServiceInstanceId(), service.getApplicationStatus());
						acrossInfo.setAcrossStatus(OK_STATUS_LIST.contains(service.getApplicationStatus().toUpperCase())? "RUNNING":"ERROR");
					}
					// 기존과 같은 상태인지 체크 후, 다른 경우에만 DB 업데이트
					if(!originalStatus.equals(acrossInfo.getAcrossStatus().toUpperCase())) acrossServiceRepository.save(acrossInfo);
				}
				
			}
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		return statusList;
	}

	/**
	 * 서비스 테이블 삭제
	 * @param List<String> serviceIdList
	 * @return Object
	 */
	public Object deleteService(List<String> serviceIdList) {
		
		String deleteYn = "Y";
		
		try {
			for(String serviceId : serviceIdList) {
				serviceInfoRepository.deleteById(serviceId);
			}
		} catch(Exception e) {
			e.printStackTrace();
			deleteYn = "N";
		}
		
		return Map.of("delete_list", "Y".equals(deleteYn)? serviceIdList.toString() : null);
	}
	
	/**
	 * 연계 서비스 테이블 워크플로우 상태 업데이트
	 * @param ServiceDTO serviceDTO
	 * @return AcrossServiceEntity
	 */
	public AcrossServiceEntity updateAcrossWorkflowState(ServiceDTO serviceDTO) {
		
		// 서비스 인포 테이블 업데이트
		AcrossServiceEntity acrossService = acrossServiceRepository.findByAcrossServiceId(serviceDTO.getAcrossServiceId());
		if(ObjectUtils.isEmpty(acrossService)) {
			throw new InvalidInputException();
		}
		if(acrossService.getAcrossStatus().contains("_FAIL") && serviceDTO.getServiceStatus().contains("_FAIL")) {
			log.info("[Workflow update] vm init failed");
			return null;
		}
		acrossService.setAcrossStatus(serviceDTO.getServiceStatus());
		acrossServiceRepository.save(acrossService);
		
		return acrossService;
	}
	
	/**
	 * 연계 서비스 테이블 삭제
	 * @param List<String> acrossServiceIdList
	 * @return Object
	 */
	public Object deleteAcrossService(List<String> acrossServiceIdList) {
		
		String deleteYn = "Y";
		
		try {
			for(String acrossServiceId : acrossServiceIdList) {
				acrossServiceRepository.deleteById(acrossServiceId);
			}
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			deleteYn = "N";
		}
		
		return Map.of("delete_list", "Y".equals(deleteYn)? acrossServiceIdList.toString() : null);
	}
}
