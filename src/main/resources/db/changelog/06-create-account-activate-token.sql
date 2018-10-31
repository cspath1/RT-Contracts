-- liquibase formatted sql

-- changeset cspath1:9
CREATE TABLE account_activate_token (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  token VARCHAR(100) NOT NULL,
  expiration_date DATETIME NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY (user_id),
  UNIQUE KEY (token)
);
-- rollback drop table account_activate_token