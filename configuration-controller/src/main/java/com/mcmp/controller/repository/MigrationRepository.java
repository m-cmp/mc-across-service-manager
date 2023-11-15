package com.mcmp.controller.repository;

import com.mcmp.controller.domain.MigrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author : Jihyeong Lee
 * @Project : mcmp-conf/controller
 * @version : 1.0.0
 * @date : 10/27/23
 * @class-description : Repository interface for migration
 *
 **/
public interface MigrationRepository extends JpaRepository<MigrationEntity, String> {

}
