-- liquibase formatted sql

--changeset cspath1:13
ALTER TABLE user ADD account_hash VARCHAR(150) NOT NULL;
CREATE UNIQUE INDEX user_account_hash_uindex ON user (account_hash);
-- ALTER TABLE user DROP account_hash;