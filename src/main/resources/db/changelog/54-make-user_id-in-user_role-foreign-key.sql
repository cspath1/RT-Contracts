-- liquibase formatted sql

-- changeset pnelson:108
ALTER TABLE user_role
ADD CONSTRAINT user_id_const
FOREIGN KEY(user_id) REFERENCES user(id);

-- rollback ALTER TABLE user_role DROP FOREIGN KEY user_id_const, DROP INDEX user_id_const;