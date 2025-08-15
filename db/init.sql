-- db/init.sql
CREATE DATABASE IF NOT EXISTS bankmanagement;
USE bankmanagement;

CREATE TABLE IF NOT EXISTS users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name  VARCHAR(100),
  phone      VARCHAR(50),
  balance DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
  transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  transaction_type ENUM('DEPOSIT','WITHDRAWAL','TRANSFER','PAYMENT') NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Optional: signup table (use only if referenced by code)
CREATE TABLE IF NOT EXISTS signup (
  email VARCHAR(255) PRIMARY KEY,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed demo user
INSERT INTO users (email, password, first_name, last_name, balance)
VALUES ('demo@bank.com', 'password123', 'Demo', 'User', 1000.00)
ON DUPLICATE KEY UPDATE email=email;
