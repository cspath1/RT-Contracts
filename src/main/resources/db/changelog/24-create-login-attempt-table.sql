-- liquibase formatted sql

-- changeset rpim:31
CREATE TABLE login_attempt (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  login_time DATETIME NOT NULL,

  PRIMARY KEY(id)
)
-- rollback drop table login_attempt
