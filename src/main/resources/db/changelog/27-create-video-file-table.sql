-- liquibase formatted sql

-- changeset jhorne:35
CREATE TABLE video_file (
    id INT(11) NOT NULL AUTO_INCREMENT,
    thumbnail_path VARCHAR(100) NOT NULL,
    video_path VARCHAR(100) NOT NULL,
    video_length VARCHAR(10) NOT NULL,
    record_created_timestamp DATETIME NOT NULL,
    record_updated_timestamp DATETIME NOT NULL,

    PRIMARY KEY(id)
);
-- rollback drop table video-file

-- changeset jhorne:36
ALTER TABLE video_file MODIFY video_length VARCHAR(10) NOT NULL;
-- rollback alter table video_file modify video_length TIME(0) not null