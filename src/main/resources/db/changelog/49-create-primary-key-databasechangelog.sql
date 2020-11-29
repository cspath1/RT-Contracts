-- liquibase formatted sql

-- changeset jlandau2:103
ALTER TABLE DATABASECHANGELOG
ADD PRIMARY KEY (ID);

-- rollback alter table DATABASECHANGELOG drop primary key;