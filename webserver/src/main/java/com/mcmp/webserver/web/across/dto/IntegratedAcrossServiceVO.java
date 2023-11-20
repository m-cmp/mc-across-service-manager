package com.mcmp.webserver.web.across.dto;

import com.mcmp.webserver.web.instance.dto.IntegratedInstanceVO;

/**
 * tb_across_service, tb_service_instance, tb_application 통합 객체
 */
public interface IntegratedAcrossServiceVO extends IntegratedInstanceVO {
	
	//tb_across_service
	String getGslbDomain();
	String getGslbCsp();
	Short getGslbWeight();
	Short getCustomerGslbWeight();
	String getMainGslb();				//Y,N
	
	//tb_vpc
	String getVpnTunnelIp();
	String getVpnTunnelCreateDate();
}
