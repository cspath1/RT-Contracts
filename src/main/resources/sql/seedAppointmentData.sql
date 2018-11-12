-- Start by seeding a telescope that is online --
INSERT INTO telescope VALUES(1, 1);

-- Create an coordinate --
INSERT INTO coordinate VALUES(1, 311.0, 69.0);

-- Then create a user --
INSERT INTO user(id, first_name, last_name, email_address, company, phone_number, password, active, status)
VALUES(1, 'Cody', 'Spath', 'cspath1@ycp.edu', 'York College of PA', '717-823-2216', 'pass1234', 1, 'ACTIVE');

-- Then an appointment --
INSERT INTO appointment(id, user_id, start_time, end_time, status, telescope_id, public, coordinate_id)
VALUES(1, 1, '2018-10-10 12:00:00', '2018-10-10 15:00:00', 'COMPLETED', 1, 1, 1);

-- Then some seed RF Data --
INSERT INTO rf_data(id, appointment_id, intensity, time_captured) VALUES(1, 1, 1, CURRENT_TIMESTAMP);
INSERT INTO rf_data(id, appointment_id, intensity, time_captured) VALUES(2, 1, 2, CURRENT_TIMESTAMP);
INSERT INTO rf_data(id, appointment_id, intensity, time_captured) VALUES(3, 1, 3, CURRENT_TIMESTAMP);
INSERT INTO rf_data(id, appointment_id, intensity, time_captured) VALUES(4, 1, 4, CURRENT_TIMESTAMP);
INSERT INTO rf_data(id, appointment_id, intensity, time_captured) VALUES(5, 1, 5, CURRENT_TIMESTAMP);
INSERT INTO rf_data(id, appointment_id, intensity, time_captured) VALUES(6, 1, 6, CURRENT_TIMESTAMP);
INSERT INTO rf_data(id, appointment_id, intensity, time_captured) VALUES(7, 1, 7, CURRENT_TIMESTAMP);
INSERT INTO rf_data(id, appointment_id, intensity, time_captured) VALUES(8, 1, 8, CURRENT_TIMESTAMP);
INSERT INTO rf_data(id, appointment_id, intensity, time_captured) VALUES(9, 1, 9, CURRENT_TIMESTAMP);
INSERT INTO rf_data(id, appointment_id, intensity, time_captured) VALUES(10, 1, 10, CURRENT_TIMESTAMP);