package com.mcmp.webserver.web.instance.dto;

public interface IntegratedInstanceVO {

	// tb_service_instance
	String getServiceInstanceId();

	String getAcrossServiceId();
	
	String getAcrossType();

	String getServiceId();

	String getVpcId();

	String getCsp();

	String getVmInstanceId();

	String getVmInstanceName();

	String getVmInstanceStatus();

	String getVmInstancePublicIp();

	String getVmInstancePrivateIp();

	String getVmInstanceCreateDate();

	String getVmMemoryType();

	String getAgentActivateYn();

	String getAgentDeployYn();

	String getAgentDeployDate();

	String getTfstateFilePath();

	String getGcpHealthcheckFlag();

	// tb_application
	String getApplicationId();

	String getApplicationName();

	String getApplicationType();

	String getApplicationActivateYn();

	String getApplicationCreateDate();
}
