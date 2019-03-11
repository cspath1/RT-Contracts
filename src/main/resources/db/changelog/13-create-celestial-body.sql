-- liquibase formatted sql

-- changeset cspath1:16
CREATE TABLE celestial_body (
  id INT(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(150) NOT NULL,
  coordinate_id INT(11) NOT NULL,
  status ENUM('HIDDEN', 'VISIBLE') NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY (coordinate_id)
)
-- rollback drop table celestial_body