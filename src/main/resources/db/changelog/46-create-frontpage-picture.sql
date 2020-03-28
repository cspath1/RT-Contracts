-- liquibase formatted sql

-- changeset jhorne:100
CREATE TABLE frontpage_picture(
    id INT(11) NOT NULL AUTO_INCREMENT,
    picture VARCHAR(100) NOT NULL,
    description VARCHAR(256) NOT NULL,
    approved tinyint(11),
    insert_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    update_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY(id)
);
-- rollback drop table frontpage_picture