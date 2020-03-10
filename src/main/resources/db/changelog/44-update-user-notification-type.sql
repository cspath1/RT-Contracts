-- liquibase formatted sql

-- changeset jhorne:98
ALTER TABLE user
ADD notification_type ENUM('EMAIL', 'SMS') AFTER profile_picture;

-- rollback alter table user drop notification_type