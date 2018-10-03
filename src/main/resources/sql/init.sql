USE radio_telescope;

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
  minLeft int

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



drop table if exists appointment;
create table appointment(
id int primary key,
type varchar(100),
assocUserId int,
starttime timestamp,
endtime timestamp,

status ENUM('Requested',
        'Scheduled',
        'InProgress',
        'Completed',
        'Canceled'),

telescopeId int,
celestialBodyId int,
orientationId int,
receiver varchar(100),
isPublic TINYINT(1) DEFAULT '1',

) ENGINE = InnoDB DEFAULT CHARSET=utf8;

drop table if exists orientation;
create table orientation(

id int,
azimuth double,
elevation double

foreign key id references appointment(orientationId)

);


drop table if exists celestialBody;
create table celestialBody(
id int,
name varchar(64),
foreign key id references appointment(celestialBodyId)

 );

create index id on appointment(id, telescopeId, celestialBodyId, isPublic, assocUserId);
