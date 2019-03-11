CREATE TABLE appointment(
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  status ENUM('REQUESTED',
              'SCHEDULED',
              'IN_PROGRESS',
              'COMPLETED',
              'CANCELED'),
  telescope_id INT(11) NOT NULL,
  coordinate_id INT(11) NOT NULL,
  public TINYINT(1) DEFAULT 1
);

CREATE TABLE celestial_body(
  id INT(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(150) NOT NULL,
  coordinate_id INT(11) DEFAULT NULL,
  status ENUM('HIDDEN', 'VISIBLE') NOT NULL
);

CREATE TABLE coordinate(
  id INT(11) NOT NULL AUTO_INCREMENT,
  right_ascension DOUBLE NOT NULL,
  declination DOUBLE NOT NULL,
  hours INT(11) NOT NULL,
  minutes INT(11) NOT NULL,
  seconds INT(11) NOT NULL
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
  affected_table ENUM('USER', 'APPOINTMENT', 'USER_ROLE'),
  action VARCHAR(100) NOT NULL,
  timestamp DATETIME NOT NULL,
  affected_record_id INT(11),
  success TINYINT(1) DEFAULT 1,
  status INT(11) NOT NULL
);

CREATE TABLE rf_data (
  id INT(11) NOT NULL AUTO_INCREMENT,
  appointment_id INT(11) NOT NULL,
  intensity INT(11) NOT NULL,
  time_captured DATETIME NOT NULL
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
  status ENUM('INACTIVE', 'ACTIVE', 'BANNED', 'DELETED')
);

CREATE TABLE user_role (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  role ENUM('User', 'Guest', 'Student', 'Researcher', 'Member', 'Admin'),
  approved TINYINT(1) DEFAULT '0'
);

CREATE TABLE reset_password_token (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  token VARCHAR(100) NOT NULL,
  expiration_date DATETIME NOT NULL
);

CREATE TABLE account_activate_token (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  token VARCHAR (100) NOT NULL,
  expiration_date DATETIME NOT NULL
);

CREATE TABLE update_email_token (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  token VARCHAR (100) NOT NULL,
  expiration_date DATETIME NOT NULL,
  email_address VARCHAR(100) NOT NULL
);

CREATE TABLE viewer (
  id INT(11) NOT NULL AUTO_INCREMENT,
  appointment_id INT(11) NOT NULL,
  user_id INT(11) NOT NULL
);