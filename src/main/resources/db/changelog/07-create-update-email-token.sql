-- liquibase formatted sql

-- changeset cspath1:10
CREATE TABLE update_email_token (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  token VARCHAR(100) NOT NULL,
  expiration_date DATETIME NOT NULL,
  email_address VARCHAR(100) NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY (user_id),
  UNIQUE KEY (token)
);
-- rollback drop table update_email_token