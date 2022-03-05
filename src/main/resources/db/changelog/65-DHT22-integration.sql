-- liquibase formatted sql

-- changeset tfranks:120

CREATE TABLE humidity (
    id INT(11) NOT NULL AUTO_INCREMENT,
    humidity FLOAT(5, 2) DEFAULT NULL,
    location INT DEFAULT NULL,
    time_captured BIGINT(20) DEFAULT NULL,
    insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id)
);

ALTER TABLE sensor_network_config
    ADD elevation_ambient_init INT NOT NULL;

ALTER TABLE sensor_overrides MODIFY COLUMN sensor_name
    ENUM(
        'GATE',
        'PROXIMITY',
        'AZIMUTH_MOTOR',
        'ELEVATION_MOTOR',
        'WEATHER_STATION',
        'ELEVATION_ABS_ENCODER',
        'AZIMUTH_ABS_ENCODER',
        'AZ_MOTOR_VIBRATION',
        'ELEV_MOTOR_VIBRATION',
        'COUNTER_BALANCE_VIBRATION',
        'EL_PROXIMITY_0',
        'EL_PROXIMITY_90',
        'AMBIENT_TEMP_HUMIDITY'
    );

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('AMBIENT_TEMP_HUMIDITY', 0);

ALTER TABLE sensor_status
    ADD ambient_temp_humidity TINYINT(4) NOT NULL;

-- rollback DROP TABLE humidity;
-- rollback ALTER TABLE sensor_network_config
-- DROP COLUMN elevation_ambient_init;
-- rollback DELETE FROM sensor_overrides WHERE
-- sensor_name = "AMBIENT_TEMP_HUMIDITY";
-- rollback ALTER TABLE sensor_status
-- DROP COLUMN ambient_temp_humidity;
