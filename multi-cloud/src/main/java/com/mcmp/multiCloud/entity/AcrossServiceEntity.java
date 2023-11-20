package com.mcmp.multiCloud.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *	연계 서비스 Entity
 */
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "tb_across_service")
public class AcrossServiceEntity {

	@Id
	@Column(name = "across_service_id", nullable = false, length = 11 )
	private Integer acrossServiceId;
	
	@Column(name = "gslb_domain", nullable = true, length = 100 )
	private String gslbDomain;
	
	@Column(name = "gslb_weight", nullable = true, length = 11 )
	private Integer gslbWeight;
	
	@Column(name = "customer_gslb_weight", nullable = true, length = 11 )
	private Integer customerGslbWeight;
	
	@Column(name = "gslb_csp", nullable = true, length = 20 )
	private String gslbCsp;
	
}
