package com.mcmp.multiCloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcmp.multiCloud.entity.AcrossServiceEntity;

@Repository
public interface AcrossServiceRepository extends JpaRepository<AcrossServiceEntity, Integer>{
	
	AcrossServiceEntity findByAcrossServiceId(Integer acrossServiceId);
}
