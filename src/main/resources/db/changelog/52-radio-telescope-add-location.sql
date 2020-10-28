-- liquibase formatted sql

-- changeset pnelson:106
ALTER TABLE radio_telescope
ADD location_id int
DEFAULT 0;

ALTER TABLE radio_telescope
ADD FOREIGN KEY(location_id) REFERENCES location(id);
-- rollback ALTER TABLE radio_telescope DROP COLUMN location;