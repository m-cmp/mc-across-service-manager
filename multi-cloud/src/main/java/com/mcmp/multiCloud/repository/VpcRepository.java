package com.mcmp.multiCloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mcmp.multiCloud.entity.VpcEntity;

public interface VpcRepository extends JpaRepository<VpcEntity, Long>{
	
	VpcEntity findOneByServiceInstanceId(String serviceInstanceId);
	
	int deleteByVpcId(Long vpcId);
}
