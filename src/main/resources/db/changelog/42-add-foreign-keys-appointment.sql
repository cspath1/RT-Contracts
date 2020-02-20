-- liquibase formatted sql

-- changeset jhorne:92
ALTER TABLE appointment
ADD FOREIGN KEY (user_id) REFERENCES user(id);

-- rollback alter table appointment drop foreign key user_id

-- changeset jhorne:93
ALTER TABLE appointment
ADD FOREIGN KEY (telescope_id) REFERENCES radio_telescope(id);

-- rollback alter table appointment drop foreign key telescope_id

-- changeset jhorne:94
ALTER TABLE appointment
ADD FOREIGN KEY (orientation_id) REFERENCES orientation(id);

-- rollback alter table appointment drop foreign key orientation_id

-- changeset jhorne:95
ALTER TABLE appointment
ADD FOREIGN KEY (spectracyber_config_id) REFERENCES spectracyber_config(id);

-- rollback alter table appointment drop foreign key spectracyber_config_id

-- changeset jhorne:96
ALTER TABLE appointment
ADD FOREIGN KEY (celestial_body_id) REFERENCES celestial_body(id);

-- rollback alter table appointment drop foreign key celestial_body_id


