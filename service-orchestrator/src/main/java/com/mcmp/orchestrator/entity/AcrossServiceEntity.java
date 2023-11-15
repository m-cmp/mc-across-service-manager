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
 *	연계 서비스 Entity 클래스
 */
@Data
@NoArgsConstructor
@Entity(name="tb_across_service")
public class AcrossServiceEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="across_service_id", nullable = false, length=11)
	private String acrossServiceId;
	
	@Column(name="across_service_name", nullable = false, length=50)
	private String acrossSeviceName;
	
	@Column(name="across_status", nullable = false, length=100)
	private String acrossStatus;
	
	@Column(name="across_type", nullable = false, length=12)
	private String acrossType;
	
	@Column(name="across_create_date", nullable = false)
	private String acrossCreateDate;
	
	@Column(name="customer_gslb_weight", nullable = true, length=11)
	private String customerGslbWeight;
	
	@Column(name="gslb_domain", nullable = true, length=255)
	private String gslbDomain;
	
	@Column(name="gslb_weight", nullable = true, length=11)
	private String gslbWeight;
	
	@JsonProperty("delete_yn")
	@ColumnDefault(value="N")
	@Column(name="delete_yn", nullable = false, length=1)
	private String deleteYn;

	@Builder
	public AcrossServiceEntity(String acrossServiceId, String acrossSeviceName, String acrossStatus, String acrossType,
			String acrossCreateDate, String customerGslbWeight, String gslbDomain, String gslbWeight, String deleteYn) {
		this.acrossServiceId = acrossServiceId;
		this.acrossSeviceName = acrossSeviceName;
		this.acrossStatus = acrossStatus;
		this.acrossType = acrossType;
		this.acrossCreateDate = acrossCreateDate;
		this.customerGslbWeight = customerGslbWeight;
		this.gslbDomain = gslbDomain;
		this.gslbWeight = gslbWeight;
		this.deleteYn = deleteYn;
	}
}
