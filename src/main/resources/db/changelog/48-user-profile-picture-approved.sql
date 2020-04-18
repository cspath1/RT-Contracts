-- liquibase formatted sql

-- changeset qherb:102
ALTER TABLE user
ADD profile_picture_approved BOOLEAN AFTER status;

-- rollback alter table user drop profile_picture