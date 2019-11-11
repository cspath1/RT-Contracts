-- liquibase formatted sql

-- changeset jlandau2:38
CREATE TABLE weather_data (
  id INT(11) NOT NULL AUTO_INCREMENT,
  wind_speed FLOAT(7,4) NOT NULL,
  wind_direction VARCHAR(100) NOT NULL,
  temperature FLOAT(7,4) NOT NULL,
  rain_rate FLOAT(7,4) NOT NULL,
  rain_total FLOAT(7,4) NOT NULL,
  rain_day FLOAT(7,4) NOT NULL,
  rain_month FLOAT(7,4) NOT NULL,
  barometric_pressure FLOAT(7,4) NOT NULL,
  dew_point FLOAT(7,4) NOT NULL,
  wind_chill FLOAT(7,4) NOT NULL,
  humidity INT(11) NOT NULL,
  heat_index INT(11) NOT NULL,

  PRIMARY KEY(id)
);
-- rollback drop table weather_data
