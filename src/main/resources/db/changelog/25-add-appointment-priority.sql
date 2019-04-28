-- liquibase formatted sql

-- changeset cspath1:31
ALTER TABLE appointment ADD priority ENUM('PRIMARY', 'SECONDARY', 'MANUAL') NOT NULL;
-- rollback alter table appointment DROP priority

-- changeset cspath1:32
UPDATE appointment a
SET a.priority = 'PRIMARY'
WHERE 1=1;
-- rollback update appointment a SET a.priority = NULL
