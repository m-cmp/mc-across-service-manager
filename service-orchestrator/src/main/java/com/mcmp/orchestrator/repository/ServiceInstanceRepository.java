package com.mcmp.orchestrator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mcmp.orchestrator.entity.ServiceInstanceEntity;

@Repository
public interface ServiceInstanceRepository extends JpaRepository<ServiceInstanceEntity, String> {
	
	ServiceInstanceEntity findOneByServiceInstanceId(String serviceInstanceId);
	
	ServiceInstanceEntity findOneByServiceId(String serviceId);
	
	List<ServiceInstanceEntity> findAllByServiceId(String serviceId);
	
	List<ServiceInstanceEntity> findAllByAcrossServiceId(String acrossServiceId);
	
	@Query(value="SELECT * FROM tb_service_instance u WHERE u.service_id IN :serviceIdList", nativeQuery=true)
	List<ServiceInstanceEntity> findAllByServiceId(@Param("serviceIdList") List<String> serviceIdList);
	
	@Query(value="SELECT * FROM tb_service_instance u WHERE u.across_service_id IN :acrossServiceIdList", nativeQuery=true)
	List<ServiceInstanceEntity> findAllByAcrossServiceId(@Param("acrossServiceIdList") List<String> acrossServiceIdList);
	
	@Query(value="SELECT * FROM tb_service_instance u WHERE u.service_instance_id IN :serviceInstanceIds", nativeQuery=true)
	List<ServiceInstanceEntity> findAllByServiceInstanceIds(@Param("serviceInstanceIds") List<String> serviceInstanceIds);
	
	@Query(value="SELECT * FROM tb_service_instance u WHERE u.service_id = :serviceId", nativeQuery=true)
	List<ServiceInstanceEntity> findAllForServiceAgent(@Param("serviceId") String serviceId);
	
	@Query(value="SELECT * FROM tb_service_instance u WHERE u.across_service_id = :acrossServiceId", nativeQuery=true)
	List<ServiceInstanceEntity> findAllForAcrossAgent(@Param("acrossServiceId") String acrossServiceId);
	
	@Query(value="SELECT COUNT(*) FROM tb_service_instance u WHERE u.service_instance_id IN :idList", nativeQuery=true)
	int getCountById(@Param("idList") List<String> serviceInstanceId);
	
	@Query(nativeQuery=true,
			value="""
						SELECT 
				 			A.*
						  FROM tb_service_instance A
						  LEFT JOIN tb_service_info B ON A.service_id = B.service_id
						  LEFT JOIN tb_across_service C ON A.across_service_id = C.across_service_id
						 WHERE (B.service_status IN ('RUNNING', 'MIGRATION_SUCCESS', 'ERROR') AND B.delete_yn != 'Y')
						    OR (C.across_status IN ('RUNNING', 'ERROR') AND C.delete_yn != 'Y')
						"""
			)
	List<ServiceInstanceEntity> findAllForHealthCheck();
}
