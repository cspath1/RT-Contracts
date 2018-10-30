-- liquibase formatted sql

-- changeset cspath1:7
CREATE TABLE rf_data (
  id INT(11) NOT NULL AUTO_INCREMENT,
  appointment_id INT(11) NOT NULL,
  intensity INT(11) NOT NULL,
  time_captured DATETIME NOT NULL,

  PRIMARY KEY (id)
);
-- rollback drop table rf_data