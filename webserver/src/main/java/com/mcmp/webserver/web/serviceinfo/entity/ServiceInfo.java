package com.mcmp.webserver.web.serviceinfo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *	서비스 Entity
 */
@Entity
@Table(name = "tb_service_info")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServiceInfo {
	
	/*
	 * 서비스 id
	 */
	@Id
	@Column(nullable = false, length = 32)
	private String serviceId;
	
	/*
	 * 서비스명
	 */
	@Column(nullable = true)
	private String serviceName;
	
	/*
	 * 서비스 템플릿 id
	 */
	@Column(nullable = false)
	private Long serviceTemplateId;
	
	/*
	 * 서비스 상태
	 * [ACTIVATE , INACTIVATE]
	 */
	@Column(nullable = false, length = 12)
	private String serviceStatus;
	
	/*
	 * 삭제 여부
	 */
	@ColumnDefault(value = "N")
	@Column(nullable = false, length = 1)
	private String deleteYn;
	
	/*
	 * 서비스 생성일시
	 */
	@Column(nullable = false)
	private LocalDateTime serviceCreateDate;

	@Builder
	public ServiceInfo(String serviceId, String serviceName, Long serviceTemplateId, String serviceStatus, String deleteYn,
			LocalDateTime serviceCreateDate) {
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.serviceTemplateId = serviceTemplateId;
		this.serviceStatus = serviceStatus;
		this.deleteYn = deleteYn;
		this.serviceCreateDate = serviceCreateDate;
	}
}
