-- SQLINES FOR EVALUATION USE ONLY (14 DAYS)
CREATE TABLE wxy_invoice (
    invoice_id    INT AUTO_INCREMENT PRIMARY KEY,
    payment_state VARCHAR(50) NOT NULL,
    ticket_id     INT NOT NULL,
    valid_state   VARCHAR(50) NOT NULL
);

ALTER TABLE wxy_invoice
    ADD CHECK ( valid_state IN ('pending','refund', 'reschedule', 'valid', 'cancelled' ) );

ALTER TABLE wxy_invoice
    ADD CHECK ( payment_state IN ( 'true', 'false' ) );

CREATE UNIQUE INDEX wxy_invoice__idx ON
    wxy_invoice (
        ticket_id
    ASC );

-- ALTER TABLE wxy_invoice ADD CONSTRAINT wxy_invoice_pk PRIMARY KEY ( invoice_id );

CREATE TABLE wxy_path (
    path_id  INT NOT NULL,
    train_id INT
);

ALTER TABLE wxy_path ADD CONSTRAINT wxy_path_pk PRIMARY KEY ( path_id );

CREATE TABLE wxy_path_station (
    station_type      VARCHAR(50) NOT NULL,
    station_id        INT NOT NULL,
    start_time        DATETIME NOT NULL,
    a_seats_available INT NOT NULL,
    b_seats_available INT NOT NULL,
    c_seats_available INT NOT NULL,
    path_id           INT NOT NULL
);

ALTER TABLE wxy_path_station
    ADD CHECK ( station_type IN ( 'END', 'START', 'STOP' ) );

CREATE TABLE wxy_station (
    station_id   INT NOT NULL,
    station_name VARCHAR(50) NOT NULL
);

ALTER TABLE wxy_station ADD CONSTRAINT wxy_station_pk PRIMARY KEY ( station_id );

CREATE TABLE wxy_ticket (
    ticket_id       INT AUTO_INCREMENT PRIMARY KEY,
    price           DECIMAL(8, 2) NOT NULL,
    seat_level      VARCHAR(50),
    date            DATE NOT NULL,
    user_id         INT NOT NULL,
    depart_station  INT NOT NULL,
    arrival_station INT NOT NULL,
    path_id         INT NOT NULL
);

-- ALTER TABLE wxy_ticket ADD CONSTRAINT wxy_ticket_pk PRIMARY KEY ( ticket_id );

CREATE TABLE wxy_train (
    train_id    INT AUTO_INCREMENT PRIMARY KEY,
    train_name  VARCHAR(50) NOT NULL,
    a_seats_num INT NOT NULL,
    b_seats_num INT NOT NULL,
    c_seats_num INT NOT NULL
);

-- ALTER TABLE wxy_train ADD CONSTRAINT wxy_train_pk PRIMARY KEY ( train_id );

CREATE TABLE wxy_user (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_name   VARCHAR(50),
    password    VARCHAR(50) NOT NULL,
    fname       VARCHAR(50),
    lname       VARCHAR(50),
    birth_date  DATETIME,
    gender      VARCHAR(50),
    nationality VARCHAR(50),
    email       VARCHAR(50),
    phone       VARCHAR(50)
);

-- ALTER TABLE wxy_user ADD CONSTRAINT wxy_user_pk PRIMARY KEY ( user_id );

ALTER TABLE wxy_invoice
    ADD CONSTRAINT wxy_invoice_wxy_ticket_fk FOREIGN KEY ( ticket_id )
        REFERENCES wxy_ticket ( ticket_id );

ALTER TABLE wxy_path_station
    ADD CONSTRAINT wxy_path_station_station_fk FOREIGN KEY ( station_id )
        REFERENCES wxy_station ( station_id );

ALTER TABLE wxy_path_station
    ADD CONSTRAINT wxy_path_station_wxy_path_fk FOREIGN KEY ( path_id )
        REFERENCES wxy_path ( path_id );

ALTER TABLE wxy_path
    ADD CONSTRAINT wxy_path_wxy_train_fk FOREIGN KEY ( train_id )
        REFERENCES wxy_train ( train_id );

ALTER TABLE wxy_ticket
    ADD CONSTRAINT wxy_ticket_wxy_path_fk FOREIGN KEY ( path_id )
        REFERENCES wxy_path ( path_id );

ALTER TABLE wxy_ticket
    ADD CONSTRAINT wxy_ticket_wxy_user_fk FOREIGN KEY ( user_id )
        REFERENCES wxy_user ( user_id );