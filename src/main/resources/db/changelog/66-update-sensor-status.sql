-- liquibase formatted sql

-- changeset lbradley1:121

ALTER TABLE sensor_status
    ADD elevation_abs_encoder TINYINT NOT NULL,
    ADD azimuth_abs_encoder TINYINT NOT NULL,
    ADD el_proximity_0 TINYINT NOT NULL,
    ADD el_proximity_90 TINYINT NOT NULL,
    ADD az_motor_temp_1 TINYINT NOT NULL,
    ADD az_motor_temp_2 TINYINT NOT NULL,
    ADD el_motor_temp_1 TINYINT NOT NULL,
    ADD el_motor_temp_2 TINYINT NOT NULL,
    ADD az_accel TINYINT NOT NULL,
    ADD el_accel TINYINT NOT NULL,
    ADD counter_balance_accel TINYINT NOT NULL,
    DROP COLUMN  proximity,
    DROP COLUMN azimuth_motor,
    DROP COLUMN elevation_motor;


--ALTER TABLE sensor_status
    --DROP COLUMN elevation_abs_encoder,
    --DROP COLUMN azimuth_abs_encoder,
    --DROP COLUMN el_proximity_0,
    --DROP COLUMN el_proximity_90,
    --DROP COLUMN az_motor_temp_1,
    --DROP COLUMN az_motor_temp_2,
    --DROP COLUMN el_motor_temp_1,
    --DROP COLUMN el_motor_temp_2,
    --DROP COLUMN az_accel,
    --DROP COLUMN el_accel,
    --DROP COLUMN counter_balance_accel,
    --ADD proximity TINYINT NOT NULL,
    --ADD azimuth_motor TINYINT NOT NULL,
    --ADD elevation_motor TINYINT NOT NULL;