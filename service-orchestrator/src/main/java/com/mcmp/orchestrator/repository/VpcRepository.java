package com.mcmp.orchestrator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mcmp.orchestrator.entity.VpcEntity;

/**
 * VPC Repository
 */
@Repository
public interface VpcRepository extends JpaRepository<VpcEntity, String> {
	
	@Query(value="SELECT * FROM tb_vpc u WHERE u.vpc_id IN :vpcIds", nativeQuery=true)
	List<VpcEntity> findAllByVpcId(@Param("vpcIds") List<String> VpcId);
}
