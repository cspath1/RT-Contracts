-- liquibase formatted sql

-- changeset cspath1:27
UPDATE appointment SET type = 'POINT';
-- rollback UPDATE appointment SET type = ''