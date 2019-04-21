-- Create another radio telescope without an associated heartbeat monitor --

INSERT INTO orientation(id, azimuth, elevation) VALUES(3, 120, 30);

INSERT INTO orientation(id, azimuth, elevation) VALUES(4, 180, 40);

INSERT INTO radio_telescope(id, online, current_orientation_id, calibration_orientation_id) VALUES (2, 1, 3, 4);