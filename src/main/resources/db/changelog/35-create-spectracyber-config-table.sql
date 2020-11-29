-- liquibase formatted sql

-- changeset jlandau2:84
CREATE TABLE spectracyber_config(
    id INT(11) NOT NULL AUTO_INCREMENT,
    mode INT(11) NOT NULL,
    integration_time INT(11) NOT NULL,
    offset_voltage DOUBLE NOT NULL,
    IF_GAIN DOUBLE NOT NULL,
    DC_GAIN INT(11) NOT NULL,
    BANDWIDTH INT(11) NOT NULL,
    insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY(id)
);
-- rollback drop table spectracyber_config