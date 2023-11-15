package com.mcmp.orchestrator.entity;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *	서비스 인포 Entity 클래스
 */
@Data
@NoArgsConstructor
@Entity(name="tb_service_info")
public class ServiceInfoEntity {
	
	@Id
	@Column(name="service_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String serviceId;
	
	@Column(name="service_name", nullable = true, length=100)
	private String serviceName;
	
	@Column(name="service_template_id", nullable = false, length=20)
	private String serviceTemplateId;
	
	@JsonProperty("state")
	@Column(name="service_status", nullable = false, length=100)
	private String serviceStatus;
	
	@JsonProperty("delete_yn")
	@ColumnDefault(value="N")
	@Column(name="delete_yn", nullable = false, length=1)
	private String deleteYn;
	
	@Column(name="service_create_date", nullable = false)
	private String serviceCreateDate;

	@Builder
	public ServiceInfoEntity(String serviceId, String serviceName, String serviceTemplateId, String serviceStatus, String deleteYn,
			String serviceCreateDate) {
		
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.deleteYn = deleteYn;
		this.serviceTemplateId = serviceTemplateId;
		this.serviceStatus = serviceStatus;
		this.serviceCreateDate = serviceCreateDate;
	}
}
