package com.mcmp.orchestrator.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * TemplateDTO - 서비스 템플릿 관련 데이터를 담는 DTO 클래스
 * 
 */
@Data
@Schema(name = "service template", description = "service template data")
public class TemplateDTO {
	
	@JsonProperty("service_template_id")
	@JsonAlias("serviceTemplateId")
	@Schema(description = "service template id")
	private String serviceTemplateId;
	
	@JsonProperty("service_template_name")
	@Schema(description = "service template name")
	private String serviceTemplateName;
	
	@JsonProperty("service_template_path")
	@Schema(description = "service template path")
	private String serviceTemplatePath;
	
	@JsonProperty("service_template_contents")
	@Schema(description = "service template contents")
	private String serviceTemplateContents;
	
	@JsonProperty("across_service_type")
	@Schema(description = "across service type, none if not across service")
	private String acrossServiceType;
	
	@JsonProperty("cloud_provider")
	@Schema(description = "list of cloud service provider")
	private List<String> cloudProvider;
	
	@JsonProperty("tf_path")
	@Schema(description = "DTO class that contains tf file path of vpc, vm and vpn")
	private TemplateTfPathDTO tfPath;
	
	@Schema(description = "map that contains application name as value")
	private Map<String, String> application;
	
	@JsonProperty("playbook_path")
	@Schema(description = "map that contains playbook path as value")
	private Map<String, String> playbookPath;
	
	@JsonProperty("vpc_id")
	@Schema(description = "list of vpc id")
	private List<String> vpcId;
}
