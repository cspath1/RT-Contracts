-- liquibase formatted sql

-- changeset cspath1:10
ALTER TABLE appointment MODIFY status enum('REQUESTED', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELED');
-- rollback ALTER TABLE appointment MODIFY status enum('Requested', 'Scheduled', 'In Progress', 'Completed', 'Canceled');