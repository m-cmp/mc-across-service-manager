package com.mcmp.multiCloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcmp.multiCloud.entity.InstanceEntity;

@Repository
public interface InstanceRepository extends JpaRepository<InstanceEntity, String> {

	InstanceEntity findByServiceInstanceId(String ServiceInstanceId);
}
