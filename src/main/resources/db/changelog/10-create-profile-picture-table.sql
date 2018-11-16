-- liquibase formatted sql

-- changeset cspath1:12
CREATE TABLE profile_picture(
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  profile_picture_url VARCHAR(150) NOT NULL,
  validated TINYINT(1) NOT NULL DEFAULT 0,

  PRIMARY KEY (id)
);
-- rollback drop table profile_picture