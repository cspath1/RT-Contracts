-- liquibase formatted sql

-- changeset pnelson:111
CREATE TABLE sensor_network_config (
  id INT(11) NOT NULL AUTO_INCREMENT,
  telescope_id INT(11) NOT NULL,
  sensor_initialization INT(11) NOT NULL,
  timeout_data_retrieval INT(11) NOT NULL,
  timeout_initialization INT(11) NOT NULL,
  insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
  update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

  PRIMARY KEY (id)
)

-- rollback drop table sensor_network_config ;