-- liquibase formatted sql

-- changeset jhorne:98
ALTER TABLE celestial_body
ADD FOREIGN KEY (coordinate_id) REFERENCES coordinate(id);

-- rollback alter table celestial_body drop foreign key coordinate_id