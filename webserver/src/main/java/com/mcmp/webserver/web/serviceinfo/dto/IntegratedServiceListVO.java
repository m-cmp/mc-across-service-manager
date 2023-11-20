package com.mcmp.webserver.web.serviceinfo.dto;

import java.util.List;

import com.mcmp.webserver.web.instance.dto.IntegratedInstanceVO;
import com.mcmp.webserver.web.template.dto.TemplateDTO;

public interface IntegratedServiceListVO extends IntegratedInstanceVO {
	
	//ROW NUMBER(React Key)
	String getRowId();
	
	//tb_service_info
	String getServiceId();
	String getServiceName();
	String getServiceTemplateId();
	String getServiceStatus();
	String getDeleteYn();
	String getServiceCreateDate();
	
	List<TemplateDTO> getTemplateDTOs();
}
