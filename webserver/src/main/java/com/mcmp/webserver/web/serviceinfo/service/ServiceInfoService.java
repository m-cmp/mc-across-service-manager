package com.mcmp.webserver.web.serviceinfo.service;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmp.webserver.exception.InvalidException;
import com.mcmp.webserver.web.serviceinfo.dto.IntegratedServiceListVO;
import com.mcmp.webserver.web.serviceinfo.dto.IntegratedServiceVO;
import com.mcmp.webserver.web.serviceinfo.repository.ServiceInfoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 서비스
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ServiceInfoService {

	@Autowired
	ServiceInfoRepository serviceInfoRepository;
	
	/**
	 * 서비스 목록 조회
	 * @return List<IntegratedServiceVO>
	 */
	public List<IntegratedServiceListVO> selectAll() {
		log.debug("[{}] {}", "ServiceInfoService", "selectAll");
		
		List<IntegratedServiceListVO> integratedServiceList = serviceInfoRepository.findIntegratedServiceList();
		
//		if(integratedServiceList.size() != 0)
//			integratedServiceList.forEach(vo -> logIntegratedServiceVO(vo, "list"));			// 쿼리 로그
//		else 
//			log.info("서비스가 없습니다.");
		
		return integratedServiceList;
	}

	/**
	 * 서비스 상세 조회
	 * @return IntegratedServiceVO
	 */
	public IntegratedServiceVO selectInstanceByServiceId(Long serviceId) {
		
		IntegratedServiceVO integratedInstance = serviceInfoRepository.findServiceInstanceOfService(serviceId);
		
		if(ObjectUtils.isNotEmpty(integratedInstance))
			logIntegratedServiceVO(integratedInstance, "detail");				// 쿼리 로그
		else 
			throw new InvalidException("해당 서비스가 존재하지 않습니다.");
		
		return integratedInstance;
	}
	
	//서비스 목록, 상세 조회 쿼리 로그
    private void logIntegratedServiceVO(IntegratedServiceVO vo, String flag) {
    	if ("list".equals(flag)) {
    		log.debug("[서비스 목록 조회] logIntegratedServiceVO ["
		            + "serviceId={}, "
		            + "serviceName={}, "
		            + "serviceTemplateId={}, "
		            + "csp={}, "
		            + "serviceStatus={}, "
		            + "deleteYn={}, "
		            + "serviceCreateDate={}]", 
		            vo.getServiceId(), 
		            vo.getServiceName(), 
		            vo.getServiceTemplateId(),
		            vo.getCsp(), 
		            vo.getServiceStatus(), 
		            vo.getDeleteYn(), 
		            vo.getServiceCreateDate());
    	} else {
    		log.debug("[서비스 상세 조회] logIntegratedServiceVO ["
                    + "serviceId={}, "
                    + "serviceName={}, "
                    + "serviceTemplateId={}, "
                    + "serviceStatus={}, "
                    + "deleteYn={}, "
                    + "serviceCreateDate={}, "
                    + "serviceInstanceId={}, "
                    + "vpcId={}, "
                    + "csp={}, "
                    + "vmInstanceId={}, "
                    + "vmInstanceName={}, "
                    + "vmInstanceStatus={}, "
                    + "vmInstancePublicIp={}, "
                    + "vmInstancePrivateIp={}, "
                    + "vmInstanceCreateDate={}, "
                    + "vmMemoryType={}, "
                    + "agentActivateYn={}, "
                    + "agentDeployYn={}, "
                    + "agentDeployDate={}, "
                    + "tfstateFilePath={}, "
                    + "gcpHealthcheckFlag={}, "
                    + "applicationId={}, "
                    + "applicationName={}, "
                    + "applicationType={}, "
                    + "applicationActivateYn={}, "
                    + "applicationCreateDate={}]", 
                    vo.getServiceId(), 
                    vo.getServiceName(), 
                    vo.getServiceTemplateId(),
                    vo.getServiceStatus(), 
                    vo.getDeleteYn(), 
                    vo.getServiceCreateDate(), 
                    vo.getServiceInstanceId(), 
                    vo.getVpcId(), 
                    vo.getCsp(), 
                    vo.getVmInstanceId(), 
                    vo.getVmInstanceName(), 
                    vo.getVmInstanceStatus(), 
                    vo.getVmInstancePublicIp(), 
                    vo.getVmInstancePrivateIp(), 
                    vo.getVmInstanceCreateDate(), 
                    vo.getVmMemoryType(), 
                    vo.getAgentActivateYn(), 
                    vo.getAgentDeployYn(), 
                    vo.getAgentDeployDate(), 
                    vo.getTfstateFilePath(), 
                    vo.getGcpHealthcheckFlag(), 
                    vo.getApplicationId(), 
                    vo.getApplicationName(), 
                    vo.getApplicationType(), 
                    vo.getApplicationActivateYn(), 
                    vo.getApplicationCreateDate());
    	}
    }
}
