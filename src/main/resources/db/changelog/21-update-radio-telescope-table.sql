-- liquibase formatted sql

-- changeset cspath1:26
ALTER TABLE radio_telescope ADD current_orientation_id INT(11) NOT NULL;
ALTER TABLE radio_telescope ADD calibration_orientation_id INT(11) NOT NULL;
-- rollback ALTER TABLE radio_telescope DROP current_orientation_id
-- rollback ALTER TABLE radio_telescope DROP calibration_orientation_id