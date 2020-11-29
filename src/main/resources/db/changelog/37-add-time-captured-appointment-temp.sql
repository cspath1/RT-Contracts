-- liquibase formatted sql

-- changeset jhorne:86
ALTER TABLE temperature
CHANGE COLUMN time time_captured BIGINT(20);
-- rollback alter temperature change column time_capture time BIGINT(20)

ALTER TABLE acceleration
CHANGE COLUMN time time_captured BIGINT(20);
-- rollback alter acceleration change column time_capture time BIGINT(20)

ALTER TABLE weather_data
ADD time_captured BIGINT(20);
-- rollback alter acceleration appointment drop time_capture