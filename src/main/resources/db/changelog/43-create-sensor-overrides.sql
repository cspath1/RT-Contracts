-- liquibase formatted sql

-- changeset jhorne:97
CREATE TABLE sensor_overrides (
    id INT(11) NOT NULL AUTO_INCREMENT,
    sensor_name ENUM('GATE', 'PROXIMITY', 'AZIMUTH_MOTOR', 'ELEVATION_MOTOR', 'WEATHER_STATION'),
    overridden TINYINT(11),
    insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id)
);

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('GATE', 0);

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('PROXIMITY', 0);

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('AZIMUTH_MOTOR', 0);

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('ELEVATION_MOTOR', 0);

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('WEATHER_STATION', 0);