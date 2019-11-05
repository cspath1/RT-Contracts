-- liquibase formatted sql

-- changeset jhorne:35
CREATE TABLE video_file (
    id INT(11) NOT NULL AUTO_INCREMENT,
    thumbnail_path VARCHAR(100) NOT NULL,
    video_path VARCHAR(100) NOT NULL,
    video_length TIME(0) NOT NULL,
    record_created_timestamp DATETIME NOT NULL,
    record_updated_timestamp DATETIME NOT NULL,

    PRIMARY KEY(id)
);
-- rollback drop table video-file
