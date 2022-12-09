-- liquibase formatted sql

-- changeset pnelson:104
ALTER TABLE radio_telescope
ADD telescope_type ENUM('SLIP_RING', 'HARD_STOPS', 'NONE')
DEFAULT 'NONE';

-- rollback ALTER TABLE radio_telescope DROP COLUMN telescope_type;