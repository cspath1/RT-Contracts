-- liquibase formatted sql

-- changeset tswann2ycp:105
ALTER TABLE user
ADD firebase_id VARCHAR(100) AFTER notification_type;

-- rollback alter table user drop firebase_id