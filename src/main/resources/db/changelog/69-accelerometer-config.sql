-- liquibase formatted sql

-- changeset tfranks:124

CREATE TABLE accelerometer_config (
    id INT(11) NOT NULL AUTO_INCREMENT,
    sensor_network_config_id INT(11) NOT NULL,
    location INT(11) NOT NULL,
    sampling_frequency FLOAT(5, 2) NOT NULL,
    g_range INT(11) NOT NULL,
    fifo_size INT(11) NOT NULL,
    x_offset INT(11) NOT NULL,
    y_offset INT(11) NOT NULL,
    z_offset INT(11) NOT NULL,
    full_bit_resolution BIT NOT NULL,
    insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id)
);

ALTER TABLE sensor_network_config
    ADD timer_period INT(11) NOT NULL,
    ADD ethernet_period INT(11) NOT NULL,
    ADD temperature_period INT(11) NOT NULL,
    ADD encoder_period INT(11) NOT NULL;

-- rollback DROP TABLE accelerometer_config;
-- rollback ALTER TABLE sensor_network_config
-- DROP COLUMN timer_period,
-- DROP COLUMN ethernet_period,
-- DROP COLUMN temperature_period,
-- DROP COLUMN encoder_period;