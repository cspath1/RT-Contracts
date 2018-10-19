CREATE DATABASE IF NOT EXISTS radio_telescope;
USE radio_telescope;

DROP TABLE IF EXISTS error;
CREATE TABLE error(
  id INT(11) NOT NULL AUTO_INCREMENT,
  log_id INT(11) NOT NULL,
  key_field VARCHAR(50) NOT NULL,
  message VARCHAR(200) NOT NULL,

  PRIMARY KEY (id),
  KEY log_id_idx (log_id),
  KEY field_idx (key_field),
  KEY message_idx (message)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS log;
CREATE TABLE log(
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11),
  affected_table ENUM('USER', 'APPOINTMENT', 'USER_ROLE', 'RF_DATA') NOT NULL,
  action ENUM('CREATE', 'RETRIEVE', 'UPDATE', 'DELETE', 'LOG_IN') NOT NULL,
  timestamp DATETIME NOT NULL,
  affected_record_id INT(11),
  success TINYINT(1) DEFAULT '1',

  PRIMARY KEY (id),
  KEY user_id_idx (user_id),
  KEY affected_table_idx (affected_table),
  KEY action_idx (action),
  KEY timestamp_idx (timestamp),
  KEY affected_record_id_idx (affected_record_id),
  KEY success_idx (success)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS rf_data;
CREATE TABLE rf_data (
  id INT(11) NOT NULL AUTO_INCREMENT,
  appointment_id INT(11) NOT NULL,
  intensity INT(11) NOT NULL,

  PRIMARY KEY (id),
  KEY appointment_id_idx (appointment_id),
  KEY intensity_idx (intensity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS telescope;
CREATE TABLE telescope (
  id INT(11) NOT NULL AUTO_INCREMENT,
  online TINYINT(1) DEFAULT '1',

  PRIMARY KEY (id),
  KEY online_idx(online)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id INT(11) NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  email_address VARCHAR(100) NOT NULL,
  company VARCHAR(100),
  phone_number VARCHAR(25),
  password VARCHAR(256),
  active TINYINT(1) DEFAULT '0',
  status ENUM('INACTIVE', 'ACTIVE', 'BANNED', 'DELETED'),

  PRIMARY KEY (id),
  UNIQUE KEY email_address (email_address),
  KEY first_name_idx (first_name),
  KEY last_name_idx (last_name),
  KEY company_idx (company),
  KEY phone_number_idx (phone_number),
  KEY active_idx (active),
  KEY status_idx (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS user_role;
CREATE TABLE user_role (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  role ENUM('User', 'Guest', 'Student', 'Researcher', 'Member', 'Admin'),
  approved TINYINT(1) DEFAULT '0',
  
  PRIMARY KEY (id),
  KEY user_id_idx (user_id),
  KEY role_idx (role),
  KEY approved_idx (approved)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS appointment;
CREATE TABLE appointment(
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
  PRIMARY KEY (id),
  KEY user_id_idx (user_id),
  KEY start_time_idx (start_time),
  KEY end_time_idx (end_time),
  KEY status_idx (status),
  KEY telescope_id_idx (telescope_id),
  KEY public_idx (public)

) ENGINE = InnoDB DEFAULT CHARSET=utf8;