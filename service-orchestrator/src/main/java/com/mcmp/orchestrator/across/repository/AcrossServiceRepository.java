package com.mcmp.orchestrator.across.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mcmp.orchestrator.entity.AcrossServiceEntity;

/**
 * 연계 서비스 Repository
 */
@Repository
public interface AcrossServiceRepository extends JpaRepository<AcrossServiceEntity, String>  {

	@Query(value="SELECT * FROM tb_across_service u WHERE u.across_service_id IN :acrossIdList", nativeQuery=true)
	List<AcrossServiceEntity> findAllByAcrossServiceIdList(@Param("acrossIdList") List<String> acrossIdList);
	
	AcrossServiceEntity findByAcrossServiceId(String acrossServiceId);
}
