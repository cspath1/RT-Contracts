-- liquibase formatted sql

-- changeset tfranks:124

ALTER TABLE rf_data MODIFY COLUMN intensity DOUBLE(18,8);

-- rollback ALTER TABLE rf_data MODIFY COLUMN intensity INT;