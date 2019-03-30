-- liquibase formatted sql

-- changeset cspath1:25
RENAME TABLE telescope TO radio_telescope;
-- rollback rename radio_telescope TO telescope