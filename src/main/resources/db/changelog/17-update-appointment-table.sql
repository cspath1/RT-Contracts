-- liquibase formatted sql

-- changeset cspath1:21
ALTER TABLE appointment ADD type VARCHAR(100) NOT NULL;
-- rollback alter table appointment drop type