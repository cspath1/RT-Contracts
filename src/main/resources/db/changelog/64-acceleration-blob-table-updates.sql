-- liquibase formatted sql

-- changeset janderson7:119

DROP TABLE acceleration_blob;

CREATE TABLE azimuth_acceleration_blob (
                                   id INT(11) NOT NULL AUTO_INCREMENT,
                                   acc_blob BLOB DEFAULT NULL,
                                   first_time_captured BIGINT(20) DEFAULT NULL,

                                   PRIMARY KEY (id)
);

CREATE TABLE elevation_acceleration_blob (
                                    id INT(11) NOT NULL AUTO_INCREMENT,
                                    acc_blob BLOB DEFAULT NULL,
                                    first_time_captured BIGINT(20) DEFAULT NULL,

                                    PRIMARY KEY (id)
);

CREATE TABLE counterbalance_acceleration_blob (
                                    id INT(11) NOT NULL AUTO_INCREMENT,
                                    acc_blob BLOB DEFAULT NULL,
                                    first_time_captured BIGINT(20) DEFAULT NULL,

                                    PRIMARY KEY (id)
);

-- rollback drop table azimuth_acceleration_blob;
-- rollback drop table elevation_acceleration_blob;
-- rollback drop table counterbalance_acceleration_blob;
