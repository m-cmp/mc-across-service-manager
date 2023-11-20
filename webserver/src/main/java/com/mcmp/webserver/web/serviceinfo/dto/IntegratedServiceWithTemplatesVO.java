package com.mcmp.webserver.web.serviceinfo.dto;

import java.util.List;

import com.mcmp.webserver.web.template.dto.TemplateDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class IntegratedServiceWithTemplatesVO implements IntegratedServiceVO {
	
	//service
	private String serviceId;
	private String serviceName;
	private String serviceTemplateId;
	private String serviceStatus;
	private String deleteYn;
	private String serviceCreateDate;
	private String serviceInstanceId;
	private String acrossServiceId;
	private String acrossType;
	private String vpcId;
	private String csp;
	//instance
	private String vmInstanceId;
	private String vmInstanceName;
	private String vmInstanceStatus;
	private String vmInstancePublicIp;
	private String vmInstancePrivateIp;
	private String vmInstanceCreateDate;
	private String vmMemoryType;
	private String agentActivateYn;
	private String agentDeployYn;
	private String agentDeployDate;
	private String tfstateFilePath;
	private String gcpHealthcheckFlag;
	//app
	private String applicationId;
	private String applicationName;
	private String applicationType;
	private String applicationActivateYn;
	private String applicationCreateDate;
	//template
	private List<TemplateDTO> templateDTOs;
	
	public IntegratedServiceWithTemplatesVO(IntegratedServiceVO vo, List<TemplateDTO> dtos) {
		// TODO Auto-generated constructor stub
		this.serviceId = vo.getServiceId();
		this.serviceName = vo.getServiceName();
		this.serviceTemplateId = vo.getServiceTemplateId();
		this.serviceStatus = vo.getServiceStatus();
		this.deleteYn = vo.getDeleteYn();
		this.serviceCreateDate = vo.getServiceCreateDate();
		this.serviceInstanceId = vo.getServiceInstanceId();
		this.acrossServiceId = vo.getAcrossServiceId();
		this.vpcId = vo.getVpcId();
		this.csp = vo.getCsp();
		this.vmInstanceId = vo.getVmInstanceId();
		this.vmInstanceName = vo.getVmInstanceName();
		this.vmInstanceStatus = vo.getVmInstanceStatus();
		this.vmInstancePublicIp = vo.getVmInstancePublicIp();
		this.vmInstancePrivateIp = vo.getVmInstancePrivateIp();
		this.vmInstanceCreateDate = vo.getVmInstanceCreateDate();
		this.vmMemoryType = vo.getVmMemoryType();
		this.agentActivateYn = vo.getAgentActivateYn();
		this.agentDeployYn = vo.getAgentDeployYn();
		this.agentDeployDate = vo.getAgentDeployDate();
		this.tfstateFilePath = vo.getTfstateFilePath();
		this.gcpHealthcheckFlag = vo.getGcpHealthcheckFlag();
		this.applicationId = vo.getApplicationId();
		this.applicationName = vo.getApplicationName();
		this.applicationType = vo.getApplicationType();
		this.applicationActivateYn = vo.getApplicationActivateYn();
		this.applicationCreateDate = vo.getApplicationCreateDate();
		this.templateDTOs = dtos;
	}
}
