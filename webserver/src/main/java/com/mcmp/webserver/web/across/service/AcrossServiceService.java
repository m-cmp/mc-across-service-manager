package com.mcmp.webserver.web.across.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmp.webserver.exception.InvalidException;
import com.mcmp.webserver.web.across.dto.AcrossServiceDTO;
import com.mcmp.webserver.web.across.dto.IntegratedAcrossServiceListVO;
import com.mcmp.webserver.web.across.dto.IntegratedAcrossServiceVO;
import com.mcmp.webserver.web.across.entity.AcrossService;
import com.mcmp.webserver.web.across.repository.AcrossServiceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 연계 서비스
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AcrossServiceService {

	@Autowired
	private final AcrossServiceRepository acrossServiceRepository;
	
	/**
	 * 연계 서비스 통합 목록 조회
	 * @return List<IntegratedAcrossServiceListVO>
	 */
	public List<IntegratedAcrossServiceListVO> selectAll() {
		log.debug("[{}] {}", "AcrossServiceService", "selectAll");
		
		List<IntegratedAcrossServiceListVO> integratedAcrossServiceList = acrossServiceRepository.findIntegratedAcrossServiceList();
		queryLog(integratedAcrossServiceList, "list");
		
		return integratedAcrossServiceList;
	}
	
	/**
	 * 연계 서비스 상세 조회
	 * @param acrossServiceId
	 * @return List<IntegratedAcrossServiceVO>
	 */
	public List<IntegratedAcrossServiceVO> selectInstanceListByAcrossServiceId(Long acrossServiceId) {
		log.info("[{}] {}", "AcrossServiceService", "selectByAcrossServiceId");
		
		List<IntegratedAcrossServiceVO> integratedInstanceList = acrossServiceRepository.findServiceInstanceListOfAcrossService(acrossServiceId);
		
		if (ObjectUtils.isEmpty(integratedInstanceList)) {
	    	throw new InvalidException("연계 서비스에 해당하는 인스턴스가 없습니다.");
		}
		queryLog(integratedInstanceList, "detail");
		
		return integratedInstanceList;
	}
	
	/**
	 * 연계 서비스 전체 목록 조회
	 * @return List<AcrossServiceDTO>
	 */
	public List<AcrossServiceDTO> selectAllByDeleteYn(String deleteYn) {
		log.debug("[{}] {}", "AcrossServiceService", "selectAllByDeleteYn");
		
		List<AcrossService> acrossServiceList = acrossServiceRepository.findAllByDeleteYn(deleteYn);
		
		return acrossServiceList.stream().map(AcrossServiceDTO::of).collect(Collectors.toList());
	}
	
	
	public <T> void queryLog(List<T> list, String flag) {
		if (list.size() != 0 && "list".equals(flag) && list.get(0) instanceof IntegratedAcrossServiceListVO) {
			list.forEach(vo -> logIntegratedAcrossServiceListVO((IntegratedAcrossServiceListVO) vo));
		} else if (list.size() != 0 && "detail".equals(flag) && list.get(0) instanceof IntegratedAcrossServiceVO) {
			list.forEach(vo -> LogIntegratedAcrossServiceVO((IntegratedAcrossServiceVO) vo));
		}
    }
	
	//연계 서비스 조회 쿼리 로그
    private void logIntegratedAcrossServiceListVO(IntegratedAcrossServiceListVO vo) {
    	log.debug("[연계 서비스 목록 조회] IntegratedAcrossServiceListVO ["
    	        + "acrossServiceId={}, "
    	        + "acrossServiceName={}, "
    	        + "acrossType={}, "
    	        + "acrossStatus={}, "
    	        + "gslbDomain={}, "
    	        + "gslbCsp={}, "
    	        + "gslbWeight={}, "
    	        + "customerGslbWeight={}, "
    	        + "deleteYn={}, "
    	        + "acrossCreateDate={}"
    	        + "serviceInstanceId={}"
    	        + "]", 
    	        vo.getAcrossServiceId(), 
    	        vo.getAcrossServiceName(), 
    	        vo.getAcrossType(), 
    	        vo.getAcrossStatus(), 
    	        vo.getGslbDomain(), 
    	        vo.getGslbCsp(), 
    	        vo.getGslbWeight(), 
    	        vo.getCustomerGslbWeight(), 
    	        vo.getDeleteYn(), 
    	        vo.getAcrossCreateDate(),
    	        vo.getServiceInstanceId());
    }
    
    //연계 서비스 상세 쿼리 로그
    private void LogIntegratedAcrossServiceVO(IntegratedAcrossServiceVO vo) {
    	log.debug("[연계 서비스 상세 조회] IntegratedAcrossServiceVO ["
    			+ "gslbDomain={}, "
                + "gslbCsp={}, "
                + "gslbWeight={}, "
                + "customerGslbWeight={}, "
                + "mainGslb={}, "
                + "vpnTunnelIp={}"
                + "serviceInstanceId={}, "
                + "acrossServiceId={}, "
                + "serviceId={}, "
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
                + "gcpHealthcheckFlag={}"
                + "applicationId={}, "
                + "applicationName={}, "
                + "applicationType={}, "
                + "applicationActivateYn={}, "
                + "applicationCreateDate={}"
                + "]",
                vo.getGslbDomain(), 
                vo.getGslbCsp(), 
                vo.getGslbWeight(), 
                vo.getCustomerGslbWeight(), 
                vo.getMainGslb(),
                vo.getVpnTunnelIp(),
                vo.getServiceInstanceId(), 
                vo.getAcrossServiceId(), 
                vo.getServiceId(), 
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
