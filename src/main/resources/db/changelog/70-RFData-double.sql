-- liquibase formatted sql

-- changeset tfranks:125

ALTER TABLE rf_data MODIFY COLUMN intensity DOUBLE(18,8);

-- rollback ALTER TABLE rf_data MODIFY COLUMN intensity INT;