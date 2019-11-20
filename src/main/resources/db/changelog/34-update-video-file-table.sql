-- liquibase formatted sql

-- changeset jhorne:80
ALTER TABLE video_file
MODIFY record_created_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)

-- rollback alter table video_file modify record_created_timestamp DATETIME not null

-- changeset jhorne:81
ALTER TABLE video_file
MODIFY record_updated_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)

-- rollback alter table video_file modify record_updated_timestamp DATETIME not null