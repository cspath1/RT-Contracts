INSERT INTO orientation(id, azimuth, elevation) VALUES(1, 120, 30);

INSERT INTO orientation(id, azimuth, elevation) VALUES(2, 180, 40);

INSERT INTO radio_telescope(id, online, current_orientation_id, calibration_orientation_id) VALUES(1, 1, 1, 2);

INSERT INTO heartbeat_monitor(id, telescope_id, last_communication) VALUES(1, 1, CURRENT_TIMESTAMP);