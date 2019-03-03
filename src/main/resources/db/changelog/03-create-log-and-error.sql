-- liquibase formatted sql

-- changeset cspath1:5
CREATE TABLE log (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11),
  affected_table VARCHAR(100) NOT NULL,
  action VARCHAR(100) NOT NULL,
  timestamp DATETIME NOT NULL,
  affected_record_id INT(11),
  success TINYINT(1) DEFAULT 1,
  status INT(11) NOT NULL,

  PRIMARY KEY (id)
);
-- rollback drop table log


-- changeset cspath1:6
CREATE TABLE error (
  id INT(11) NOT NULL AUTO_INCREMENT,
  log_id INT(11) NOT NULL,
  key_field VARCHAR(50) NOT NULL,
  message VARCHAR(200) NOT NULL,

  PRIMARY KEY (id)
);
-- rollback drop table error