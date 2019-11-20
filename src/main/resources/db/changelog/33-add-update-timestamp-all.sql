-- liquibase formatted sql

-- changeset jlandau2:62
ALTER TABLE account_activate_token
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table account_activate_token drop update_timestamp

-- changeset jlandau2:63
ALTER TABLE allotted_time_cap
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table allotted_time_cap drop update_timestamp

-- changeset jlandau2:64
ALTER TABLE appointment
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table appointment drop update_timestamp

-- changeset jlandau2:65
ALTER TABLE celestial_body
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table celestial_body drop update_timestamp

-- changeset jlandau2:66
ALTER TABLE coordinate
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table coordinate drop update_timestamp

-- changeset jlandau2:67
ALTER TABLE error
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table error drop update_timestamp

-- changeset jlandau2:68
ALTER TABLE feedback
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table feedback drop update_timestamp

-- changeset jlandau2:69
ALTER TABLE log
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table log drop update_timestamp

-- changeset jlandau2:70
ALTER TABLE login_attempt
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table login_attempt drop update_timestamp

-- changeset jlandau2:71
ALTER TABLE orientation
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table orientation drop update_timestamp

-- changeset jlandau2:72
ALTER TABLE radio_telescope
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table radio_telescope drop update_timestamp

-- changeset jlandau2:73
ALTER TABLE reset_password_token
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table reset_password_token drop update_timestamp

-- changeset jlandau2:74
ALTER TABLE rf_data
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table rf_data drop update_timestamp

-- changeset jlandau2:75
ALTER TABLE update_email_token
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table update_email_token drop update_timestamp

-- changeset jlandau2:76
ALTER TABLE user
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table user drop update_timestamp

-- changeset jlandau2:77
ALTER TABLE user_role
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table user_role drop update_timestamp

-- changeset jlandau2:78
ALTER TABLE viewer
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table viewer drop update_timestamp

-- changeset jlandau2:79
ALTER TABLE weather_data
ADD update_timestamp TIMESTAMP(6)  DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);
-- rollback alter table weather_data drop update_timestamp