-- liquibase formatted sql

-- changeset jhorne:80
ALTER TABLE video_file
MODIFY record_created_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);

-- rollback alter table video_file modify record_created_timestamp DATETIME not null

-- changeset jhorne:81
ALTER TABLE video_file
MODIFY record_updated_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- rollback alter table video_file modify record_updated_timestamp DATETIME not null

-- changeset jhorne:82
ALTER TABLE video_file
CHANGE COLUMN record_created_timestamp insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);

-- rollback alter table video_file change column insert_timestamp record_created_timestamp TIMESTAMP(6) default CURRENT_TIMESTAMP(6)

-- changeset jhorne:83
ALTER TABLE video_file
CHANGE COLUMN record_updated_timestamp update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- rollback alter table video_file change column update_timestamp record_updated_timestamp TIMESTAMP(6) default CURRENT_TIMESTAMP(6) on update CURRENT_TIMESTAMP(6)