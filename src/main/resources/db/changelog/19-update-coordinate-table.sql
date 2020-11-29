-- liquibase formatted sql

-- changeset cspath1:25
ALTER TABLE coordinate ADD appointment_id INT(11) DEFAULT NULL;
-- rollback alter table coordinate drop appointment_id