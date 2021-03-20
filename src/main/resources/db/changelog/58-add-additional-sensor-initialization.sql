-- liquibase formatted sql

-- changeset pnelson:113

ALTER TABLE sensor_network_config
    DROP sensor_initialization,
    ADD elevation_temp_1_init INT(11) NOT NULL AFTER timeout_initialization,
    ADD elevation_temp_2_init INT(11) NOT NULL AFTER timeout_initialization,
    ADD azimuth_temp_1_init INT(11) NOT NULL AFTER timeout_initialization,
    ADD azimuth_temp_2_init INT(11) NOT NULL AFTER timeout_initialization,
    ADD azimuth_accelerometer_init INT(11) NOT NULL AFTER timeout_initialization,
    ADD elevation_accelerometer_init INT(11) NOT NULL AFTER timeout_initialization,
    ADD counterbalance_accelerometer_init INT(11) NOT NULL AFTER timeout_initialization,
    ADD azimuth_encoder_init INT(11) NOT NULL AFTER timeout_initialization,
    ADD elevation_encoder_init INT(11) NOT NULL AFTER timeout_initialization;

-- rollback delete from sensor_overrides where
-- sensor_name = "EL_PROXIMITY_90" or
-- sensor_name = "EL_PROXIMITY_0" or
-- sensor_name = "COUNTER_BALANCE_VIBRATION" or
-- sensor_name = "ELEV_MOTOR_VIBRATION" or
-- sensor_name = "AZ_MOTOR_VIBRATION" or
-- sensor_name = "AZIMUTH_ABS_ENCODER" or
-- sensor_name = "ELEVATION_ABS_ENCODER";