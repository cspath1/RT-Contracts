-- liquibase formatted sql

-- changeset jhorne:101
ALTER TABLE rf_data
ADD FOREIGN KEY (appointment_id) REFERENCES appointment(id);

-- rollback alter table rf_data drop foreign key appointment_id