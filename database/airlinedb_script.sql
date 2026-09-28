-- =============================================================================
-- airlinedb: database creation and sample data (MySQL 8.0)
--
-- Bookings (customer <-> flight) are NOT stored here: each customer has a
-- binary bookings file whose location is stored in customer.path.
-- After running this script, run flightreservations.SampleDataSeeder to
-- rebuild the matching sample booking files in data/customers/.
-- =============================================================================

DROP DATABASE IF EXISTS airlinedb;
CREATE DATABASE airlinedb CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE airlinedb;

CREATE TABLE airline (
    airlineId   INT AUTO_INCREMENT PRIMARY KEY,
    airlineName VARCHAR(100) NOT NULL,
    country     VARCHAR(100) NOT NULL,
    iataCode    CHAR(2)      NOT NULL,
    CONSTRAINT uq_airline_iata UNIQUE (iataCode)
) ENGINE = InnoDB;

CREATE TABLE customer (
    customerId   INT AUTO_INCREMENT PRIMARY KEY,
    customerName VARCHAR(100) NOT NULL,
    email        VARCHAR(100) NOT NULL,
    phoneNumber  VARCHAR(20)  NOT NULL,
    path         VARCHAR(255) NULL COMMENT 'Bookings file, set in the same transaction as the insert',
    CONSTRAINT uq_customer_email UNIQUE (email)
) ENGINE = InnoDB;

CREATE TABLE flight (
    flightId      INT AUTO_INCREMENT PRIMARY KEY,
    origin        VARCHAR(100) NOT NULL,
    destination   VARCHAR(100) NOT NULL,
    departureDate DATE         NOT NULL,
    seatAmount    INT          NOT NULL COMMENT 'Seats still available',
    travelClass   ENUM('ECONOMY', 'BUSINESS', 'FIRST') NOT NULL,
    airlineId     INT          NOT NULL,
    CONSTRAINT fk_flight_airline FOREIGN KEY (airlineId) REFERENCES airline (airlineId)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_flight_seats CHECK (seatAmount >= 0),
    CONSTRAINT chk_flight_route CHECK (origin <> destination),
    INDEX idx_flight_departure (departureDate)
) ENGINE = InnoDB;

-- -----------------------------------------------------------------------------
-- Sample data. Explicit ids keep the sample booking files in sync.
-- -----------------------------------------------------------------------------

INSERT INTO airline (airlineId, airlineName, country, iataCode) VALUES
    (1, 'Iberia',     'Spain',   'IB'),
    (2, 'Vueling',    'Spain',   'VY'),
    (3, 'Air France', 'France',  'AF'),
    (4, 'Lufthansa',  'Germany', 'LH'),
    (5, 'Ryanair',    'Ireland', 'FR');

-- Customer 4 has no bookings yet: the file is created on the first booking.
INSERT INTO customer (customerId, customerName, email, phoneNumber, path) VALUES
    (1, 'Laura García',     'laura.garcia@example.com',     '+34 600 111 222',   'data/customers/1/flights.dat'),
    (2, 'Mikel Etxeberria', 'mikel.etxeberria@example.com', '+34 600 333 444',   'data/customers/2/flights.dat'),
    (3, 'John Smith',       'john.smith@example.com',       '+44 7700 900123',   'data/customers/3/flights.dat'),
    (4, 'Marie Dubois',     'marie.dubois@example.com',     '+33 6 12 34 56 78', 'data/customers/4/flights.dat');

-- Dates are relative to the day the script runs, so past and future flights
-- never go stale. seatAmount already discounts the sample bookings:
--   customer 1 -> flights 1, 4, 5 | customer 2 -> flights 2, 3, 8 | customer 3 -> flights 1, 7
INSERT INTO flight (flightId, origin, destination, departureDate, seatAmount, travelClass, airlineId) VALUES
    (1, 'Bilbao',    'Madrid',    CURDATE() - INTERVAL 60 DAY, 118, 'ECONOMY',  1),
    (2, 'Madrid',    'Paris',     CURDATE() - INTERVAL 20 DAY,  19, 'BUSINESS', 3),
    (3, 'Barcelona', 'Berlin',    CURDATE() - INTERVAL 5 DAY,  149, 'ECONOMY',  4),
    (4, 'Bilbao',    'London',    CURDATE() + INTERVAL 10 DAY, 179, 'ECONOMY',  5),
    (5, 'Madrid',    'New York',  CURDATE() + INTERVAL 30 DAY,   7, 'FIRST',    1),
    (6, 'Barcelona', 'Rome',      CURDATE() + INTERVAL 45 DAY,   1, 'ECONOMY',  2),
    (7, 'Paris',     'Frankfurt', CURDATE() + INTERVAL 60 DAY,   0, 'BUSINESS', 3),
    (8, 'Bilbao',    'Barcelona', CURDATE() + INTERVAL 90 DAY, 179, 'ECONOMY',  2);
