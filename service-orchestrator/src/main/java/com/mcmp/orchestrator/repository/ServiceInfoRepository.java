package com.mcmp.orchestrator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mcmp.orchestrator.entity.ServiceInfoEntity;

/**
 * 서비스 인포 Repository
 */
@Repository
public interface ServiceInfoRepository extends JpaRepository<ServiceInfoEntity, String> {
	
	ServiceInfoEntity findByServiceId(String serviceId);
	
	@Query(value="SELECT * FROM tb_service_info u WHERE u.service_id IN :serviceIdList", nativeQuery=true)
	List<ServiceInfoEntity> findAllByServiceIdList(@Param("serviceIdList") List<String> serviceIdList);
}
