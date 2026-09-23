DROP DATABASE IF EXISTS airlinedb ;
CREATE DATABASE airlinedb;
USE airlinedb;

CREATE TABLE AIRLINE (
	airlineId INT AUTO_INCREMENT PRIMARY KEY,
    airlineName VARCHAR (100),
	country VARCHAR(100),
    iataCode VARCHAR (10)
);

CREATE TABLE CUSTOMER (
	customerId INT AUTO_INCREMENT PRIMARY KEY,
    customerName VARCHAR (100),
    email VARCHAR (40), 
    phoneNumber VARCHAR (10),
    path VARCHAR (100)
);

CREATE TABLE FLIGHT (
    flightId INT AUTO_INCREMENT PRIMARY KEY,
    origin VARCHAR (100),
    destination VARCHAR (100),
    departureDate  DATE,
    seatAmount INT,
    travelClass ENUM('ECONOMY', 'BUSINESS', 'FIRST'),
    airlineId INT,
	FOREIGN KEY (airlineId) REFERENCES airline (airlineId)
);
    
CREATE TABLE BOOK (
	customerId INT NOT NULL,
    flightId INT NOT NULL,
    PRIMARY KEY (customerId, flightId),
	FOREIGN KEY (customerId) REFERENCES customer (customerId),
    FOREIGN KEY (flightId) REFERENCES flight (flightId)
);    
