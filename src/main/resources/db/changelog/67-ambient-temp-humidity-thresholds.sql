-- liquibase formatted sql

-- changeset tfranks:122

ALTER TABLE thresholds
    ADD minimum FLOAT(7, 4);

ALTER TABLE thresholds MODIFY COLUMN sensor_name
    ENUM(
    'WIND',
    'AZ_MOTOR_TEMP',
    'ELEV_MOTOR_TEMP',
    'AZ_MOTOR_VIBRATION',
    'ELEV_MOTOR_VIBRATION',
    'AZ_MOTOR_CURRENT',
    'ELEV_MOTOR_CURRENT',
    'COUNTER_BALANCE_VIBRATION',
    'AMBIENT_TEMP',
    'AMBIENT_HUMIDITY'
    );


--ALTER TABLE sensor_status
    --DROP COLUMN minimum;
-- rollback DELETE FROM sensor_overrides WHERE
-- sensor_name = "AMBIENT_HUMIDITY" OR sensor_name = "AMBIENT_TEMP";