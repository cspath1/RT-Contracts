insert into radio_telescope.radio_telescope(current_orientation_id, calibration_orientation_id)
    values(0, 0);

insert into radio_telescope.user(first_name, last_name, email_address, company, phone_number, password, active, notification_type, status)
    values('Joel', 'Horne', 'jhorne@ycp.edu', 'YCP', 7173310624, '$2a$13$xLseo621qyZ6wjDGWRfDme/0rFbgTV4g/217eKRMKcRyZcLgUPzYi', 1, 'SMS', 'ACTIVE');

insert into radio_telescope.user_role(user_id, role, approved)
    values(1, 'USER', 1);

insert into radio_telescope.user_role(user_id, role, approved)
    values(1, 'ADMIN', 1);