-- liquibase formatted sql

-- changeset cspath1:3
CREATE TABLE appointment (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  status ENUM('Requested',
        'Scheduled',
        'InProgress',
        'Completed',
        'Canceled'),
  telescope_id INT(11) NOT NULL,
  public TINYINT(1) DEFAULT '1',

  PRIMARY KEY (id)
);
-- rollback drop table appointment

-- changeset cspath1:4
CREATE TABLE telescope (
  id INT(11) NOT NULL AUTO_INCREMENT,
  online TINYINT(1) DEFAULT '1',

  PRIMARY KEY (id)
);
--rollback drop table telescope