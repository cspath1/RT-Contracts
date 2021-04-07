-- liquibase formatted sql

-- changeset DanPalm5:114

ALTER TABLE user MODIFY firebase_id varchar(500);

-- rollback ALTER TABLE user MODIFY firebase_id varchar(100);
