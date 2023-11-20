package com.mcmp.orchestrator.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
// import java.io.File;
import org.springframework.stereotype.Component;

import com.mcmp.orchestrator.dto.ServiceInstanceDTO;
import com.mcmp.orchestrator.dto.TemplateDTO;
import com.mcmp.orchestrator.dto.TfPathDTO;
import com.mcmp.orchestrator.entity.VpcEntity;

import lombok.extern.slf4j.Slf4j;


/**
 * 받은 template 파일 필요한 형태로 파싱
 * 
 * @details template 파일을 파싱하는 클래스
 * @author 오승재
 *
 */
@Slf4j
@Component
public class TemplateParser {
	
	@Value("${templates.base-dir}")
    private String baseDir;
	
	/**
	 * 서비스/연계서비스 생성 시 파싱
	 * @param TemplateDTO template
	 * @return List<ServiceInstanceDTO>
	 */
	public List<ServiceInstanceDTO> parse(TemplateDTO template) {
		
	    List<ServiceInstanceDTO> instanceList = new ArrayList<>();
	    String uuid;
	    for(String csp : template.getCloudProvider()) {
	    	csp = csp.toLowerCase();
	    	uuid = csp + CommonUtil.makeUUID();
	    	TfPathDTO tfPath = TfPathDTO.builder()
	    			.vpc(baseDir + template.getTfPath().getVpc().getOrDefault(csp.toUpperCase(), ""))
	    			.vm(baseDir + template.getTfPath().getVm().getOrDefault(csp.toUpperCase(), ""))
	    			.vgw(ObjectUtils.isEmpty(template.getTfPath().getVgw())? "" : baseDir + template.getTfPath().getVgw().getOrDefault(csp.toUpperCase(), ""))
	    			.vpn(ObjectUtils.isEmpty(template.getTfPath().getVpn())? "" : baseDir + template.getTfPath().getVpn().getOrDefault(csp.toUpperCase(), ""))
	    			.gslb(ObjectUtils.isEmpty(template.getTfPath().getGslb())? "" : baseDir + template.getTfPath().getGslb().getOrDefault(csp.toUpperCase(), ""))
	    			.build();
	    			
	    	ServiceInstanceDTO service = ServiceInstanceDTO.builder()
	    			.serviceInstanceId(uuid)
	    			.csp(csp.toUpperCase())
	    			.tfPath(tfPath)
	    			.applicationType(template.getApplication().getOrDefault(csp.toUpperCase(), ""))
	    			.build();
	    	instanceList.add(service);
	    }
	    
	    log.info("[TemplateParser] List<ServiceInstanceDTO> : {}", instanceList);
	    return instanceList;
	}
	
	/**
	 * VPC를 재활용하여 연계서비스 생성 시 파싱
	 * @param TemplateDTO template, List<VpcEntity> vpcList
	 * @return List<ServiceInstanceDTO>
	 */
	public List<ServiceInstanceDTO> parse(TemplateDTO template, List<VpcEntity> vpcList) {
		log.info("[TemplateParser] vpcList : {}", vpcList);
		
	    List<ServiceInstanceDTO> instanceList = new ArrayList<>();
	    String uuid;
	    for(String csp : template.getCloudProvider()) {
	    	String lowerCsp = csp.toLowerCase();
	    	VpcEntity vpc = vpcList.stream().filter(a -> a.getCsp().toLowerCase().equals(lowerCsp)).findAny().orElse(null);
	    	if(ObjectUtils.isEmpty(vpc)) {
	    		uuid = csp + CommonUtil.makeUUID();
	    	} else {
	    		uuid = vpc.getServiceInstanceId();
	    	}
	    	
	    	TfPathDTO tfPath = TfPathDTO.builder()
	    			.vpc(baseDir + template.getTfPath().getVpc().getOrDefault(csp.toUpperCase(), ""))
	    			.vm(baseDir + template.getTfPath().getVm().getOrDefault(csp.toUpperCase(), ""))
	    			.vgw(ObjectUtils.isEmpty(template.getTfPath().getVgw())? "" : baseDir + template.getTfPath().getVgw().getOrDefault(csp.toUpperCase(), ""))
	    			.vpn(ObjectUtils.isEmpty(template.getTfPath().getVpn())? "" : baseDir + template.getTfPath().getVpn().getOrDefault(csp.toUpperCase(), ""))
	    			.gslb(ObjectUtils.isEmpty(template.getTfPath().getGslb())? "" : baseDir + template.getTfPath().getGslb().getOrDefault(csp.toUpperCase(), ""))
	    			.build();
	    			
	    	ServiceInstanceDTO service = ServiceInstanceDTO.builder()
	    			.serviceInstanceId(uuid)
	    			.csp(csp.toUpperCase())
	    			.tfPath(tfPath)
	    			.applicationType(template.getApplication().getOrDefault(csp.toUpperCase(), ""))
	    			.build();
	    	instanceList.add(service);
	    }
	    
	    log.info("[TemplateParser] List<ServiceInstanceDTO> : {}", instanceList);
	    return instanceList;
	}

	/**
	 * 서비스 마이그레이션 시 템플릿에서 어플리케이션 정보 제외 파싱
	 * @param TemplateDTO template
	 * @return ServiceInstanceDTO
	 */
	public ServiceInstanceDTO parseForMigration(TemplateDTO template) {
		
	    ServiceInstanceDTO service = null;
		String uuid;
		for(String csp : template.getCloudProvider()) {
			csp = csp.toLowerCase();
			uuid = csp + CommonUtil.makeUUID();
			TfPathDTO tfPath = TfPathDTO.builder()
					.vpc(baseDir + template.getTfPath().getVpc().getOrDefault(csp.toUpperCase(), ""))
					.vm(baseDir + template.getTfPath().getVm().getOrDefault(csp.toUpperCase(), ""))
					.build();
					
			service = ServiceInstanceDTO.builder()
					.serviceInstanceId(uuid)
					.csp(csp.toUpperCase())
					.tfPath(tfPath)
					.build();
		}
		
		log.info("[TemplateParser] ServiceInstanceDTO : {}", service);
		return service;
	}
}
