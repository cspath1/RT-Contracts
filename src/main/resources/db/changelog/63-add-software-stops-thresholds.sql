-- liquibase formatted sql

-- changeset tfranks:118

ALTER TABLE radio_telescope
    ADD elevation_max_degree float(5,2) NOT NULL,
    ADD elevation_min_degree float(5,2) NOT NULL;

-- rollback ALTER TABLE radio_telescope
-- DROP COLUMN elevation_max_degree,
-- DROP COLUMN elevation_min_degree;
