-- liquibase formatted sql

-- changeset dmchugh:115

CREATE TABLE acceleration_blob (
  id INT(11) NOT NULL AUTO_INCREMENT,
  acc_blob LONGTEXT DEFAULT NULL,
  time_captured BIGINT(20) DEFAULT NULL,
  insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
  update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

  PRIMARY KEY (id)
);

-- rollback drop table acceleration_blob;
