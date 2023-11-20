package com.mcmp.webserver.web.vpc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mcmp.webserver.web.vpc.entity.VPC;

public interface VPCRepository extends JpaRepository<VPC, Long> {

	//VPC 전체 목록 조회
	public List<VPC> findAll();
	
//	VPCEntity findOneByServiceInstanceId(String serviceInstanceId);
}
