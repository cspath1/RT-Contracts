-- liquibase formatted sql

-- changeset dmchugh:116

ALTER TABLE acceleration
          MODIFY acceleration_magnitude int,
          MODIFY acceleration_x int,
          MODIFY acceleration_y int,
          MODIFY acceleration_z int;

-- rollback ALTER TABLE acceleration
-- MODIFY acceleration_magnitude float(4,3),
-- MODIFY acceleration_x float(4,3),
-- MODIFY acceleration_y float(4,3),
-- MODIFY acceleration_z float(4,3);
