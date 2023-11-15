package com.mcmp.controller.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author : Jihyeong Lee
 * @Project : mcmp-conf/controller
 * @version : 1.0.0
 * @date : 10/27/23
 * @class-description : Entity class for migration
 *
 **/
@Entity
@Table(name = "tb_migration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MigrationEntity {

    @Id
    @Column(name = "migration_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String migrationId;

    @Column(name = "source_instance_id")
    private String sourceInstanceId;

    @Column(name = "db_dump_file_path")
    private String dumpFilePath;

    @Column(name = "target_instance_id")
    private String targetInstanceId;


}
