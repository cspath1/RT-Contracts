-- liquibase formatted sql

-- changeset jlandau2:58

CREATE TABLE sensor_status(
    id INT(11) NOT NULL AUTO_INCREMENT,
    gate tinyint NOT NULL,
    proximity tinyint NOT NULL,
    azimuth_motor tinyint NOT NULL,
    elevation_motor tinyint NOT NULL,
    weather_station tinyint NOT NULL,
    insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY(id)
);
-- rollback drop table sensor_status

-- changeset jlandau2:59
CREATE TABLE vibration(
    id INT(11) NOT NULL AUTO_INCREMENT,
    vibration_data BLOB NOT NULL,
    FFT_start_time LONG,
    FFT_end_time LONG,
    start_frequency FLOAT(7,3),
    frequency_step_per_division FLOAT(7,3),
    number_points INT(16),
    location INT(16),
    insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY(id)
);
-- rollback drop table vibration

-- changeset jlandau2:60
CREATE TABLE acceleration(
    id INT(11) NOT NULL AUTO_INCREMENT,
    acceleration_magnitude FLOAT(4,3),
    acceleration_x FLOAT(4,3),
    acceleration_y FLOAT(4,3),
    acceleration_z FLOAT(4,3),
    location INT(16),
    time LONG,
    insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY(id)
);
-- rollback drop table acceleration

-- changeset jlandau2:61
CREATE TABLE temperature(
    id INT(11) NOT NULL AUTO_INCREMENT,
    temperature FLOAT(5,2),
    location INT(16),
    time LONG,
    insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY(id)
);
-- rollback drop table temperature