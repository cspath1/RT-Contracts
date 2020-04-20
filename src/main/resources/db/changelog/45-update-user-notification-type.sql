-- liquibase formatted sql

-- changeset jhorne:99
ALTER TABLE user
ADD notification_type ENUM('EMAIL', 'SMS', 'ALL') AFTER profile_picture;

-- rollback alter table user drop notification_type