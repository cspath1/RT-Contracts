-- liquibase formatted sql

-- changeset pnelson1:105
CREATE TABLE location (
  id INT(11) NOT NULL AUTO_INCREMENT,
  latitude double NOT NULL,
  longitude double NOT NULL,
  altitude double NOT NULL,
  name varchar(100),
  insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
  update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

  PRIMARY KEY (id)
)
-- rollback drop table location