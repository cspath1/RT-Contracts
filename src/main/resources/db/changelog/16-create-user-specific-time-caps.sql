-- liquibase formatted sql

-- changeset cspath1:19
CREATE TABLE allotted_time_cap (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  allotted_time INT DEFAULT 0,

  PRIMARY KEY (id)
);
-- rollback drop table allotted_time_cap

-- changeset cspath1:20
INSERT INTO allotted_time_cap (user_id, allotted_time)
SELECT id, 18000000 FROM user;
-- rollback delete from allotted_time_cap