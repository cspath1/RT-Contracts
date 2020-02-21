-- liquibase formatted sql

-- changeset jhorne:89
CREATE TABLE thresholds (
    id INT(11) NOT NULL AUTO_INCREMENT,
    sensor_name ENUM('WIND', 'AZ_MOTOR_TEMP', 'ELEV_MOTOR_TEMP', 'AZ_MOTOR_VIBRATION', 'ELEV_MOTOR_VIBRATION', 'AZ_MOTOR_CURRENT', 'ELEV_MOTOR_CURRENT', 'COUNTER_BALANCE_VIBRATION'),
    maximum FLOAT(7, 4),
    insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id)
);

INSERT INTO thresholds(sensor_name, maximum)
    VALUES('WIND', 30.0);

INSERT INTO thresholds(sensor_name, maximum)
    VALUES('AZ_MOTOR_TEMP', 80.0);

INSERT INTO thresholds(sensor_name, maximum)
    VALUES('ELEV_MOTOR_TEMP', 80.0);

INSERT INTO thresholds(sensor_name, maximum)
    VALUES('AZ_MOTOR_VIBRATION', 1.0);

INSERT INTO thresholds(sensor_name, maximum)
    VALUES('ELEV_MOTOR_VIBRATION', 1.0);

INSERT INTO thresholds(sensor_name, maximum)
    VALUES('AZ_MOTOR_CURRENT', 6.0);

INSERT INTO thresholds(sensor_name, maximum)
    VALUES('ELEV_MOTOR_CURRENT', 6.0);

INSERT INTO thresholds(sensor_name, maximum)
    VALUES('COUNTER_BALANCE_VIBRATION', 0.42);

-- rollback drop table thresholds