-- liquibase formatted sql

-- changeset cspath1:31
CREATE TABLE heartbeat_monitor (
    id INT(11) NOT NULL AUTO_INCREMENT,
    last_communication DATETIME NOT NULL,
    telescope_id INT(11) NOT NULL,

    PRIMARY KEY(id)
);
-- rollback drop table heartbeat_monitor

-- changeset cspath1:32
INSERT INTO heartbeat_monitor(last_communication, telescope_id)
SELECT CURRENT_TIMESTAMP(), id FROM radio_telescope;
-- rollback delete from heartbeat_monitor