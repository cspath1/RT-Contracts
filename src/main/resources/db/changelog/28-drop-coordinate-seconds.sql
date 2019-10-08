-- liquibase formatted sql

<<<<<<< HEAD
-- changeset jlandau2:35
ALTER TABLE coordinate DROP COLUMN seconds;
-- rollback alter table coordinate add secondsINT(11) NOT NULL;

=======
-- changeset jhorne:34
CREATE TABLE video_file (
    id INT(11) NOT NULL AUTO_INCREMENT,
    thumbnail_path VARCHAR(100) NOT NULL,
    video_path VARCHAR(100) NOT NULL,
    video_length TIME(0) NOT NULL,
    record_created_timestamp DATETIME NOT NULL,
    record_updated_timestamp DATETIME NOT NULL,

    PRIMARY KEY(id)
);
>>>>>>> f48a270d10bab014473cad9f048bd7a2192eef77
-- rollback drop table video-file
