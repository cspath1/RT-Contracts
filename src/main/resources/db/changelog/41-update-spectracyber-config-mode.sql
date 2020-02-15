-- liquibase formatted sql

-- changeset jhorne:90
ALTER TABLE spectracyber_config
    MODIFY COLUMN mode ENUM('UNKNOWN', 'SPECTRAL', 'CONTINUUM') NOT NULL;

-- rollback alter table spectracyber_config modify column mode int(11) not null

-- changeset jhorne:91
ALTER TABLE spectracyber_config
    MODIFY COLUMN integration_time DOUBLE NOT NULL;

-- rollback alter table spectracyber_config modify column integration_time int(11) not null