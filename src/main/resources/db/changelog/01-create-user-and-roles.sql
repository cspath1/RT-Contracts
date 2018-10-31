-- liquibase formatted sql

-- changeset cspath1:1
CREATE TABLE user (
  id INT(11) NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  email_address VARCHAR(100) NOT NULL,
  company VARCHAR(100),
  phone_number VARCHAR(25),
  password VARCHAR(256),
  active TINYINT(1) DEFAULT '0',
  status ENUM('Inactive', 'Active', 'Banned', 'Deleted'),

  PRIMARY KEY (id),
  UNIQUE KEY (email_address)
);
-- rollback drop table user

-- changeset cspath1:2
CREATE TABLE user_role (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  role ENUM('USER', 'GUEST', 'STUDENT', 'RESEARCHER', 'MEMBER', 'ADMIN'),
  approved TINYINT(1) DEFAULT '0',

  PRIMARY KEY (id)
);
-- rollback drop table user_role