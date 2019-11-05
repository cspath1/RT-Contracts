-- liquibase formatted sql

-- changeset cspath1:28
UPDATE appointment SET type = 'POINT';
-- rollback UPDATE appointment SET type = ''