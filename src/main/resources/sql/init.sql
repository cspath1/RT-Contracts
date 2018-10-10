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
  affected_table ENUM('USER', 'APPOINTMENT') NOT NULL,
  action ENUM('CREATE', 'RETRIEVE', 'UPDATE', 'DELETE') NOT NULL,
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
  role ENUM('Guest', 'Student', 'Researcher', 'Member', 'Admin'),
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
  celestial_body_id INT(11) NOT NULL,
  orientation_id INT(11) NOT NULL,
  public TINYINT(1) DEFAULT '1'

) ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE orientation(
appt_id INT NOT NULL,
azimuth INT NOT NULL,
elevation INT NOT NULL,

KEY azimuth_idx (azimuth),
KEY elevation_idx (elevation)

) ENGINE = InnoDB DEFAULT CHARSET=utf8;