-- liquibase formatted sql

-- changeset pnelson:112

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
        'EL_PROXIMITY_90'
    );

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('ELEVATION_ABS_ENCODER', 0);

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('AZIMUTH_ABS_ENCODER', 0);

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('AZ_MOTOR_VIBRATION', 0);

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('ELEV_MOTOR_VIBRATION', 0);

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('COUNTER_BALANCE_VIBRATION', 0);

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('EL_PROXIMITY_0', 0);

INSERT INTO sensor_overrides(sensor_name, overridden)
    VALUES('EL_PROXIMITY_90', 0);

-- rollback delete from sensor_overrides where
-- sensor_name = "EL_PROXIMITY_90" or
-- sensor_name = "EL_PROXIMITY_0" or
-- sensor_name = "COUNTER_BALANCE_VIBRATION" or
-- sensor_name = "ELEV_MOTOR_VIBRATION" or
-- sensor_name = "AZ_MOTOR_VIBRATION" or
-- sensor_name = "AZIMUTH_ABS_ENCODER" or
-- sensor_name = "ELEVATION_ABS_ENCODER";