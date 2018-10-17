-- Start by seeding a telescope that is online --
INSERT INTO telescope VALUES(1, 1);

-- Then create a user --
INSERT INTO user(id, first_name, last_name, email_address, company, phone_number, password, active, status)
VALUES(1, 'Cody', 'Spath', 'cspath1@ycp.edu', 'York College of PA', '717-823-2216', 'pass1234', 1, 'Active');

-- Then an appointment --
INSERT INTO appointment(id, user_id, start_time, end_time, status, telescope_id, public)
VALUES(1, 1, '2018-10-10 12:00:00', '2018-10-10 15:00:00', 'Completed', 1, 1);

-- Then some seed RF Data --
INSERT INTO rf_data(id, appointment_id, intensity) VALUES(1, 1, 1);
INSERT INTO rf_data(id, appointment_id, intensity) VALUES(2, 1, 2);
INSERT INTO rf_data(id, appointment_id, intensity) VALUES(3, 1, 3);
INSERT INTO rf_data(id, appointment_id, intensity) VALUES(4, 1, 4);
INSERT INTO rf_data(id, appointment_id, intensity) VALUES(5, 1, 5);
INSERT INTO rf_data(id, appointment_id, intensity) VALUES(6, 1, 6);
INSERT INTO rf_data(id, appointment_id, intensity) VALUES(7, 1, 7);
INSERT INTO rf_data(id, appointment_id, intensity) VALUES(8, 1, 8);
INSERT INTO rf_data(id, appointment_id, intensity) VALUES(9, 1, 9);
INSERT INTO rf_data(id, appointment_id, intensity) VALUES(10, 1, 10);