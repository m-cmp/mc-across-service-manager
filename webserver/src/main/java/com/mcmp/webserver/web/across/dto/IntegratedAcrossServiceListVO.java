package com.mcmp.webserver.web.across.dto;

import com.mcmp.webserver.web.instance.dto.IntegratedInstanceVO;

/**
 * tb_across_service, tb_service_instance, tb_application 통합 객체
 */
public interface IntegratedAcrossServiceListVO extends IntegratedInstanceVO {
	
	//tb_across_service
	String getAcrossServiceId();
	String getAcrossType();
	String getAcrossServiceName();
	String getAcrossStatus();
	String getGslbDomain();
	String getGslbCsp();
	Short getGslbWeight();
	Short getCustomerGslbWeight();
	String getDeleteYn();
	String getAcrossCreateDate();
	
}
