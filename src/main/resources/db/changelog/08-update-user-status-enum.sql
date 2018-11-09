-- liquibase formatted sql

-- changeset cspath1:10
ALTER TABLE user MODIFY status enum('INACTIVE', 'ACTIVE', 'BANNED', 'DELETED');
-- rollback ALTER TABLE appointment MODIFY status enum('Inactive', 'Active', 'Banned', 'Deleted')