-- liquibase formatted sql

-- changeset cspath1:18
CREATE TABLE feedback (
  id INT(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) DEFAULT NULL,
  priority INT(11) NOT NULL,
  comments TEXT NOT NULL,

  PRIMARY KEY (id)
)
-- rollback drop table feedback