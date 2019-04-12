-- liquibase formatted sql

-- changeset cspath1:21
CREATE TABLE orientation (
  id INT(11) NOT NULL AUTO_INCREMENT,
  azimuth DOUBLE NOT NULL,
  elevation DOUBLE NOT NULL,

  PRIMARY KEY (id)
);
-- rollback drop table orientation

-- changeset cspath1:22
ALTER TABLE appointment ADD orientation_id INT(11) DEFAULT NULL;
-- rollback alter table appointment drop orientation_id