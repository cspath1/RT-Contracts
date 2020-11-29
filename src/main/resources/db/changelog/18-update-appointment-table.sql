-- liquibase formatted sql

-- changeset cspath1:23
ALTER TABLE appointment ADD type VARCHAR(100) NOT NULL;
-- rollback alter table appointment drop type

-- changeset cspath1:24
ALTER TABLE appointment ADD celestial_body_id INT(11) DEFAULT NULL;
-- rollback alter table appointment drop celestial_body_id