-- liquibase formatted sql

-- changeset cspath1:15
ALTER TABLE coordinate ADD hours INT(11) NOT NULL;
ALTER TABLE coordinate ADD minutes INT(11) NOT NULL;
ALTER TABLE coordinate ADD seconds INT(11) NOT NULL;
-- rollback ALTER TABLE coordinate DROP hours
-- rollback ALTER TABLE coordinate DROP minutes
-- rollback ALTER TABLE coordinate DROP seconds