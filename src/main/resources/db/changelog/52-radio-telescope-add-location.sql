-- liquibase formatted sql

-- changeset pnelson:106
ALTER TABLE radio_telescope
ADD location_id int(11)
DEFAULT 0;

-- rollback ALTER TABLE radio_telescope DROP COLUMN location;