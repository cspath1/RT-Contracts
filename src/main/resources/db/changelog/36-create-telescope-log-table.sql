-- liquibase formatted sql

-- changeset jlandau2:85
CREATE TABLE telescope_log(
    id INT(11) NOT NULL AUTO_INCREMENT,
    log_date DATETIME NOT NULL,
    log_level LONGTEXT NOT NULL,
    thread LONGTEXT NOT NULL,
    logger LONGTEXT NOT NULL,
    message LONGTEXT NOT NULL,
    insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY(id)
);
-- rollback drop table telescope_log