INSERT INTO orientation(id, azimuth, elevation) VALUES(1, 120, 30);

INSERT INTO orientation(id, azimuth, elevation) VALUES(2, 180, 40);

INSERT INTO radio_telescope(id, online, current_orientation_id, calibration_orientation_id) VALUES(1, 1, 1, 2);

INSERT INTO telescope_log(id, log_date, log_level, thread, logger, message) 
VALUES (1, CURRENT_TIME, 'log_level', 'thread', 'logger', 'message')