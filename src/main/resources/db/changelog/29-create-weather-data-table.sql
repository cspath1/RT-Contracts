-- liquibase formatted sql

-- changeset jlandau2:38
CREATE TABLE weather_data (
  id INT(11) NOT NULL AUTO_INCREMENT,
  wind_speed FLOAT(7,4) NOT NULL,
  wind_direction_deg FLOAT(6,3) NOT NULL,
  wind_direction_str tinytext NOT NULL,
  outside_temperature_degF FLOAT(6,3) NOT NULL,
  inside_temperature_degF FLOAT(6,3) NOT NULL,
  rain_rate FLOAT(6,3) NOT NULL,
  rain_total FLOAT(6,3) NOT NULL,
  rain_day FLOAT(6,3) NOT NULL,
  rain_month FLOAT(6,3) NOT NULL,
  barometric_pressure FLOAT(6,3) NOT NULL,
  dew_point FLOAT(6,3) NOT NULL,
  wind_chill FLOAT(6,3) NOT NULL,
  humidity  FLOAT(6,3) NOT NULL,
  heat_index FLOAT(6,3) NOT NULL,

  PRIMARY KEY(id)
);
-- rollback drop table weather_data
