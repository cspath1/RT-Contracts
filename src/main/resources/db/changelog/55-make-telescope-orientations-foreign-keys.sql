-- liquibase formatted sql

-- changeset pnelson:109
ALTER TABLE radio_telescope
ADD CONSTRAINT current_orientation_id_const
FOREIGN KEY(current_orientation_id) REFERENCES orientation(id);

-- rollback ALTER TABLE radio_telescope DROP FOREIGN KEY current_orientation_id_const, DROP INDEX current_orientation_id_const;

-- changeset pnelson:110
ALTER TABLE radio_telescope
ADD CONSTRAINT calibration_orientation_id_const
FOREIGN KEY(calibration_orientation_id) REFERENCES orientation(id);

-- rollback ALTER TABLE radio_telescope DROP FOREIGN KEY calibration_orientation_id_const, DROP INDEX calibration_orientation_id_const;
