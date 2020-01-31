-- liquibase formatted sql

-- changeset jhorne:86
ALTER TABLE user
ADD profile_picture VARCHAR(100) AFTER status;

-- rollback alter table user drop profile_picture