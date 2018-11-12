-- liquibase formatted sql

-- changeset cspath1:13
CREATE TABLE coordinate (
  id INT(11) NOT NULL AUTO_INCREMENT,
  right_ascension DOUBLE NOT NULL,
  declination DOUBLE NOT NULL,

  PRIMARY KEY(id)
);
-- rollback drop table coordinate

-- changeset cspath1:14
ALTER TABLE appointment ADD coordinate_id INT(11) DEFAULT NULL;
-- rollback alter table appointment drop coordinate_id