package com.mcmp.orchestrator.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mcmp.orchestrator.entity.ApplicationEntity;

/**
 * 어플리케이션 Repository
 */
@Repository
public interface AppRepository extends JpaRepository<ApplicationEntity, String> {
	
	ApplicationEntity findOneByServiceInstanceId(String serviceInstanceId);
	
	@Query(value="SELECT * FROM tb_application u WHERE u.service_instance_id IN :serviceInstanceIdList", nativeQuery=true)
	List<ApplicationEntity> findAllByServiceInstanceId(@Param("serviceInstanceIdList") List<String> serviceInstanceIdList);
	
	@Query(value="DELETE FROM tb_application WHERE service_instance_id IN :serviceInstanceIdList", nativeQuery=true)
	void deleteAllByServiceInstanceIdList(@Param("serviceInstanceIdList") List<String> serviceInstanceIdList);
}
