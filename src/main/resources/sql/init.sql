CREATE DATABASE IF NOT EXISTS radio_telescope;
USE radio_telescope;

DROP TABLE IF EXISTS account_activate_token;
CREATE TABLE account_activate_token (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  token VARCHAR(100) NOT NULL,
  expiration_date DATETIME NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY (user_id),
  UNIQUE KEY (token),
  KEY expiration_date_idx (expiration_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS error;
CREATE TABLE error (
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
CREATE TABLE log (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11),
  affected_table VARCHAR(100) NOT NULL,
  action VARCHAR(100) NOT NULL,
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

DROP TABLE IF EXISTS reset_password_token;
CREATE TABLE reset_password_token (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  token VARCHAR(100) NOT NULL,
  expiration_date DATETIME NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY (user_id),
  UNIQUE KEY (token),
  KEY expiration_date_idx (expiration_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS rf_data;
CREATE TABLE rf_data (
  id INT(11) NOT NULL AUTO_INCREMENT,
  appointment_id INT(11) NOT NULL,
  intensity INT(11) NOT NULL,
  time_captured DATETIME NOT NULL,

  PRIMARY KEY (id),
  KEY appointment_id_idx (appointment_id),
  KEY intensity_idx (intensity),
  KEY time_captured_idx (time_captured)
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
  status ENUM('Inactive', 'Active', 'Banned', 'Deleted'),

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
  role ENUM('USER', 'GUEST', 'STUDENT', 'RESEARCHER', 'MEMBER', 'ADMIN'),
  approved TINYINT(1) DEFAULT '0',
  
  PRIMARY KEY (id),
  KEY user_id_idx (user_id),
  KEY role_idx (role),
  KEY approved_idx (approved)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS appointment;
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
  PRIMARY KEY (id),
  KEY user_id_idx (user_id),
  KEY start_time_idx (start_time),
  KEY end_time_idx (end_time),
  KEY status_idx (status),
  KEY telescope_id_idx (telescope_id),
  KEY public_idx (public)

) ENGINE = InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS viewer;
CREATE TABLE viewer(

recipient_user_id INT(11) NOT NULL,
sharing_user_id INT(11) NOT NULL,
shared_appointment_id INT(11) NOT NULL,

KEY recipient_user_id_idx (recipient_user_id),
KEY sharing_user_id_idx (sharing_user_id),
KEY shared_appointment_id_idx (shared_appointment_id)


) ENGINE = InnoDB DEFAULT CHARSET=utf8;