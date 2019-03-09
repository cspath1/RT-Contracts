-- liquibase formatted sql

-- changeset cspath1:17
CREATE TABLE viewer (
  id INT(11) NOT NULL AUTO_INCREMENT,
  appointment_id INT(11) NOT NULL,
  user_id INT(11) NOT NULL,

  PRIMARY KEY (id)
)
-- rollback drop table viewer