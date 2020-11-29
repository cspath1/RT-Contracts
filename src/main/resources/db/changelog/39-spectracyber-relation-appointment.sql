-- liquibase formatted sql

-- changeset jhorne:88
ALTER TABLE appointment
ADD spectracyber_config_id INT(11) NOT NULL AFTER orientation_id;

-- rollback alter table appointment drop spectracyber_config_id