-- liquibase formatted sql

-- changeset jhorne:85
ALTER TABLE user_role
MODIFY COLUMN role ENUM('USER', 'GUEST', 'STUDENT', 'RESEARCHER', 'MEMBER', 'ADMIN', 'ALUMNUS');

-- rollback alter table user_role modify column 'role' enum('USER', 'GUEST', 'STUDENT', 'RESEARCHER', 'MEMBER', 'ADMIN')

