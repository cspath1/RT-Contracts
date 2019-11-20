-- liquibase formatted sql

-- changeset jlandau2:57
CREATE TABLE weather_threshold(
    id INT(11) NOT NULL AUTO_INCREMENT,
    wind_speed SMALLINT DEFAULT 25,
    snow_dump_time SMALLINT DEFAULT 30,

    PRIMARY KEY(id)
);
-- rollback drop table weather_threshold

