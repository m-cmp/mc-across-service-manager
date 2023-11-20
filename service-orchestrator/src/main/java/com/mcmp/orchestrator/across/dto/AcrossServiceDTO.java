package com.mcmp.orchestrator.across.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class AcrossServiceDTO {
	
	@Id
	@Column(name="across_service_id")
	private String acrossServiceId;
	
	@Column(name="across_service_name")
	private String acrossServiceName;
	
	@Column(name="across_create_date")
	private String acrossCreateDate;
}
