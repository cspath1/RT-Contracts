-- liquibase formatted sql

-- changeset cspath1:29
UPDATE coordinate c
INNER JOIN appointment a ON a.coordinate_id = c.id
SET c.appointment_id = a.id
WHERE 1=1;
-- rollback UPDATE coordinate SET appointment_id NULL

-- changeset cspath1:30
ALTER TABLE appointment DROP COLUMN coordinate_id
-- rollback ALTER TABLE appointment ADD coordinate_id INT(11) NOT NULL