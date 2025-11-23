-- MazeBank Online Banking System Database Schema
-- Created for Java GUI Application with JDBC

-- Create Database
CREATE DATABASE IF NOT EXISTS mazebank_db;
USE mazebank_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    role ENUM('ADMIN', 'CUSTOMER') DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Accounts Table
CREATE TABLE IF NOT EXISTS accounts (
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type ENUM('SAVINGS', 'CURRENT', 'FIXED_DEPOSIT') DEFAULT 'SAVINGS',
    balance DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    from_account_id INT,
    to_account_id INT,
    transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'LOAN_PAYMENT', 'INVESTMENT') NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    description VARCHAR(255),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'COMPLETED',
    FOREIGN KEY (from_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL,
    FOREIGN KEY (to_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL
);

-- Banking Services Table
CREATE TABLE IF NOT EXISTS banking_services (
    service_id INT PRIMARY KEY AUTO_INCREMENT,
    service_name VARCHAR(100) NOT NULL,
    service_type ENUM('LOAN', 'INVESTMENT', 'INSURANCE', 'CREDIT_CARD') NOT NULL,
    description TEXT,
    interest_rate DECIMAL(5, 2),
    is_active BOOLEAN DEFAULT TRUE
);

-- Customer Services Table (Link between customers and services)
CREATE TABLE IF NOT EXISTS customer_services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    service_id INT NOT NULL,
    application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    amount DECIMAL(15, 2),
    duration_months INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES banking_services(service_id) ON DELETE CASCADE
);

-- System Settings Table
CREATE TABLE IF NOT EXISTS system_settings (
    setting_id INT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) UNIQUE NOT NULL,
    setting_value TEXT,
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert Default Admin User (password: admin123)
INSERT INTO users (username, password, full_name, email, role) 
VALUES ('admin', 'admin123', 'System Administrator', 'admin@mazebank.com', 'ADMIN');

-- Insert Sample Banking Services
INSERT INTO banking_services (service_name, service_type, description, interest_rate) VALUES
('Personal Loan', 'LOAN', 'Get instant personal loans up to $50,000', 8.5),
('Home Loan', 'LOAN', 'Finance your dream home with competitive rates', 7.2),
('Fixed Deposit', 'INVESTMENT', 'Secure investment with guaranteed returns', 6.5),
('Premium Credit Card', 'CREDIT_CARD', 'Premium credit card with exclusive benefits', 0.0);

-- Insert Default System Settings
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
('MIN_BALANCE', '500.00', 'Minimum account balance required'),
('MAX_DAILY_WITHDRAWAL', '10000.00', 'Maximum daily withdrawal limit'),
('TRANSFER_FEE', '0.00', 'Transaction fee for transfers'),
('BANK_NAME', 'MazeBank', 'Official bank name'),
('SUPPORT_EMAIL', 'support@mazebank.com', 'Customer support email');

-- Create Indexes for better performance
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_account_number ON accounts(account_number);
CREATE INDEX idx_transaction_date ON transactions(transaction_date);
CREATE INDEX idx_transaction_type ON transactions(transaction_type);
