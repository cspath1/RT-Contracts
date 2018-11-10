-- liquibase formatted sql

--changeset cspath1:13
ALTER TABLE user ADD account_hash VARCHAR(150) DEFAULT NULL;
CREATE UNIQUE INDEX account_hash ON user (account_hash);
-- ALTER TABLE user DROP account_hash;