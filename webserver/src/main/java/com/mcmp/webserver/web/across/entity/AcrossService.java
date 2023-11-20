package com.mcmp.webserver.web.across.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *	연계 서비스 Entity
 */
@Entity
@Table(name = "tb_across_service")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AcrossService {
	
	/*
	 * 연계 서비스 id
	 */
	@Id
	@Column(nullable = false, length = 20)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long acrossServiceId;
	
	/*
	 * 연계 서비스명
	 */
	@Column(nullable = false, length = 50)
	private String acrossServiceName;
	
	/*
	 * 연계 서비스 상태
	 */
	@Column(nullable = false, length = 12)
	private String acrossStatus;
	
	/*
	 * 연계 서비스 타입
	 */
	@Column(nullable = false, length = 12, columnDefinition = "varchar(12) default 'VPN'")
	private String acrossType;
	
	/*
	 * 삭제 여부
	 */
	@ColumnDefault(value = "N")
	@Column(nullable = false, length = 1)
	private String deleteYn;
	
	/*
	 * 연계 서비스 생성일시
	 */
	@Column(nullable = false)
	private LocalDateTime acrossCreateDate;

	@Builder
	public AcrossService(Long acrossServiceId, String acrossServiceName, String acrossStatus, String acrossType,
			String deleteYn, LocalDateTime acrossCreateDate) {
		this.acrossServiceId = acrossServiceId;
		this.acrossServiceName = acrossServiceName;
		this.acrossStatus = acrossStatus;
		this.acrossType = acrossType;
		this.deleteYn = deleteYn;
		this.acrossCreateDate = acrossCreateDate;
	}
}
