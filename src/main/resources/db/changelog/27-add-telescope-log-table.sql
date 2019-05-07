-- liquibase formatted sql

-- changeset cspath1:34
CREATE TABLE telescope_log (
    id INT(11) NOT NULL AUTO_INCREMENT,
    log_date DATETIME NOT NULL,
    log_level VARCHAR(100) NOT NULL,
    thread VARCHAR(100) NOT NULL,
    logger VARCHAR(100) NOT NULL,
    message VARCHAR(255) NOT NULL,

    PRIMARY KEY (id)
);
-- rollback drop table telescope_log
