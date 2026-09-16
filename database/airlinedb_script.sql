DROP DATABASE IF EXISTS airlinedb ;
CREATE DATABASE airlinedb;
USE airlinedb;

CREATE TABLE AIRLINE (
	airline_id INT AUTO_INCREMENT PRIMARY KEY,
    airlineName VARCHAR (100),
	country VARCHAR(100),
    iata_code VARCHAR (10)
);

CREATE TABLE CUSTOMER (
	customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customerName VARCHAR (100),
    email VARCHAR (20), 
    phoneNumber VARCHAR (10),
    route VARCHAR (100)
);

CREATE TABLE FLIGHT (
    flight_id INT AUTO_INCREMENT PRIMARY KEY,
    origin VARCHAR (100),
    destination VARCHAR (100),
    departure_date  DATE,
    num_seats INT,
    travel_class ENUM('ECONOMY', 'BUSINESS', 'FIRST'),
    airline_id INT,
	FOREIGN KEY (airline_id) REFERENCES airline (airline_id)
);
    
CREATE TABLE BOOK (
	customer_id INT NOT NULL,
    flight_id INT NOT NULL,
    PRIMARY KEY (customer_id, flight_id),
	FOREIGN KEY (customer_id) REFERENCES customer (customer_id),
    FOREIGN KEY (flight_id) REFERENCES flight (flight_id)
);    
