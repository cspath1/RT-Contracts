-- liquibase formatted sql

-- changeset jhorne:89
CREATE TABLE thresholds (
    id INT(11) NOT NULL AUTO_INCREMENT,
    wind INT(11) NOT NULL,
    az_motor_temp INT(11) NOT NULL,
    elev_motor_temp INT(11) NOT NULL,
    az_motor_vibration INT(11) NOT NULL,
    elev_motor_vibration INT(11) NOT NULL,
    az_motor_current INT(11) NOT NULL,
    elev_motor_current INT(11) NOT NULL,
    counter_balance_vibration FLOAT(7, 4) NOT NULL,
    insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id)
);

INSERT INTO thresholds(wind, az_motor_temp, elev_motor_temp, az_motor_vibration, elev_motor_vibration, az_motor_current, elev_motor_current, counter_balance_vibration)
    VALUES(30, 80, 80, 1, 1, 6, 6, 0.42);

-- rollback drop table thresholds