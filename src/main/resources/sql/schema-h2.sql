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
  public TINYINT(1) DEFAULT 1
);

CREATE TABLE error (
  id        INT(11)      NOT NULL AUTO_INCREMENT,
  log_id    INT(11)      NOT NULL,
  key_field VARCHAR(50)  NOT NULL,
  message   VARCHAR(200) NOT NULL
);

CREATE TABLE log(
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11),
  affected_table ENUM('USER', 'APPOINTMENT', 'USER_ROLE') NOT NULL,
  action ENUM('CREATE', 'RETRIEVE', 'UPDATE', 'DELETE', 'LOG_IN') NOT NULL,
  timestamp DATETIME NOT NULL,
  affected_record_id INT(11),
  success TINYINT(1) DEFAULT '1'
);

CREATE TABLE telescope (
  id INT(11) NOT NULL AUTO_INCREMENT,
  online TINYINT(1) DEFAULT '1'
);

CREATE TABLE user (
  id INT(11) NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  email_address VARCHAR(100) NOT NULL,
  company VARCHAR(100),
  phone_number VARCHAR(25),
  password VARCHAR(256),
  active TINYINT(1) DEFAULT '0',
  status ENUM('Inactive', 'Active', 'Banned', 'Deleted')
);

CREATE TABLE user_role (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  role ENUM('User', 'Guest', 'Student', 'Researcher', 'Member', 'Admin'),
  approved TINYINT(1) DEFAULT '0'
)