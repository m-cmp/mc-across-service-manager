package com.mcmp.controller.repository;

import com.mcmp.controller.domain.AppEntity;
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
 * @class-description : Repository interface for application
 *
 **/
public interface AppRepository extends JpaRepository <AppEntity, String> {

    /**
     *
     * @date : 10/27/23
     * @author : Jihyeong Lee
     * @method-description : find application list by application id
     *
     **/
    @Query(value = "SELECT * FROM tb_application a WHERE a.application_id IN :idList", nativeQuery = true)
    List<AppEntity> findByIdList(@Param("idList") List<String> idList);
    AppEntity findByServiceInstanceId(String serviceInstanceId);


}
