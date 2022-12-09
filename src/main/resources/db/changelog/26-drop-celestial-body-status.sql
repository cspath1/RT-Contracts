-- liquibase formatted sql

-- changeset cspath1:34
ALTER TABLE celestial_body DROP COLUMN status;
-- rollback alter table celestial_body add status enum('HIDDEN', 'VISIBLE') not null default 'VISIBLE'
