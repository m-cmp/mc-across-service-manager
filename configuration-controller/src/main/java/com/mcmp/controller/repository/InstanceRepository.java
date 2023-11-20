package com.mcmp.controller.repository;

import com.mcmp.controller.domain.InstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 * @author : Jihyeong Lee
 * @Project : mcmp-conf/controller
 * @version : 1.0.0
 * @date : 10/27/23
 * @class-description : Repository interface for vm_instance
 *
 **/
public interface InstanceRepository extends JpaRepository <InstanceEntity, String> {

    /**
     *
     * @date : 10/27/23
     * @author : Jihyeong Lee
     * @method-description : find vm_instance list by instance id
     *
     **/
    @Query(value = "SELECT * FROM tb_service_instance u WHERE u.service_instance_id IN :idList", nativeQuery = true)
    List<InstanceEntity> findByIdList(@Param("idList") List<String> idList);
}
