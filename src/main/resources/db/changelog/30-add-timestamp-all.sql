-- liquibase formatted sql

-- changeset jlandau2:39
ALTER TABLE account_activate_token
ADD insert_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table account_activate_token drop insert_timestamp

-- changeset jlandau2:40
ALTER TABLE allotted_time_cap
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table allotted_time_cap drop insert_timestamp

-- changeset jlandau2:41
ALTER TABLE appointment
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table appointment drop insert_timestamp

-- changeset jlandau2:42
ALTER TABLE celestial_body
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table celestial_body drop insert_timestamp

-- changeset jlandau2:43
ALTER TABLE coordinate
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table coordinate drop insert_timestamp

-- changeset jlandau2:44
ALTER TABLE error
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table error drop insert_timestamp

-- changeset jlandau2:45
ALTER TABLE feedback
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table feedback drop insert_timestamp

-- changeset jlandau2:46
ALTER TABLE log
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table log drop insert_timestamp

-- changeset jlandau2:47
ALTER TABLE login_attempt
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table login_attempt drop insert_timestamp

-- changeset jlandau2:48
ALTER TABLE orientation
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table orientation drop insert_timestamp

-- changeset jlandau2:49
ALTER TABLE radio_telescope
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table radio_telescope drop insert_timestamp

-- changeset jlandau2:50
ALTER TABLE reset_password_token
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table reset_password_token drop insert_timestamp

-- changeset jlandau2:51
ALTER TABLE rf_data
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table rf_data drop insert_timestamp

-- changeset jlandau2:52
ALTER TABLE update_email_token
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table update_email_token drop insert_timestamp

-- changeset jlandau2:53
ALTER TABLE user
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table user drop insert_timestamp

-- changeset jlandau2:54
ALTER TABLE user_role
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table user_role drop insert_timestamp

-- changeset jlandau2:55
ALTER TABLE viewer
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table viewer drop insert_timestamp

-- changeset jlandau2:56
ALTER TABLE weather_data
ADD insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6);
-- rollback alter table weather_data drop insert_timestamp