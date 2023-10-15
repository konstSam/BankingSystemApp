DROP DATABASE bankingsystem;
-- Create the banking database
CREATE DATABASE IF NOT EXISTS bankingsystem;

-- Use the banking database
USE bankingsystem;

CREATE TABLE IF NOT EXISTS user (
    `userid` INT AUTO_INCREMENT PRIMARY KEY,
    `userName` VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS bank (
    `bankid` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS bankaccount (
    `accountNumber` INT(10) AUTO_INCREMENT PRIMARY KEY,
    `currencyType` VARCHAR(3),
    `accountType` VARCHAR(50),
    `balance` FLOAT,
    `userid` INT,
    `bankid` INT,
    FOREIGN KEY (`userid`) REFERENCES `user`(`userid`),
    FOREIGN KEY (`bankid`) REFERENCES `bank`(`bankid`)
);

CREATE TABLE IF NOT EXISTS transactions (
    `transactionid` INT AUTO_INCREMENT PRIMARY KEY,
    `accountNumber` INT,
    `transactionType` VARCHAR(255) NOT NULL,
    `amount` FLOAT NOT NULL,
    `date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`accountNumber`) REFERENCES bankaccount(`accountNumber`)
);

