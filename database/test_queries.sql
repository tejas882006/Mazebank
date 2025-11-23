-- MazeBank - Useful SQL Queries for Testing and Verification

-- ==========================================
-- VERIFICATION QUERIES
-- ==========================================

-- Check if database exists
SHOW DATABASES LIKE 'mazebank_db';

-- Use the database
USE mazebank_db;

-- Show all tables
SHOW TABLES;

-- Verify table structures
DESCRIBE users;
DESCRIBE accounts;
DESCRIBE transactions;
DESCRIBE banking_services;
DESCRIBE customer_services;
DESCRIBE system_settings;

-- ==========================================
-- DATA VERIFICATION
-- ==========================================

-- Check default admin user
SELECT * FROM users WHERE role = 'ADMIN';

-- Count total users
SELECT COUNT(*) as total_users FROM users;

-- Count customers
SELECT COUNT(*) as total_customers FROM users WHERE role = 'CUSTOMER';

-- Count accounts
SELECT COUNT(*) as total_accounts FROM accounts;

-- Count transactions
SELECT COUNT(*) as total_transactions FROM transactions;

-- View all banking services
SELECT * FROM banking_services;

-- Check system settings
SELECT * FROM system_settings;

-- ==========================================
-- USER QUERIES
-- ==========================================

-- View all users
SELECT user_id, username, full_name, email, role, is_active, created_at 
FROM users 
ORDER BY created_at DESC;

-- View active users only
SELECT user_id, username, full_name, email, role 
FROM users 
WHERE is_active = TRUE;

-- View customers only
SELECT user_id, username, full_name, email, phone 
FROM users 
WHERE role = 'CUSTOMER';

-- Find user by username
SELECT * FROM users WHERE username = 'admin';

-- Find user by email
SELECT * FROM users WHERE email = 'admin@mazebank.com';

-- ==========================================
-- ACCOUNT QUERIES
-- ==========================================

-- View all accounts with user details
SELECT 
    a.account_id,
    a.account_number,
    a.account_type,
    a.balance,
    u.full_name as account_holder,
    u.email,
    a.created_at
FROM accounts a
JOIN users u ON a.user_id = u.user_id
ORDER BY a.created_at DESC;

-- View accounts for specific user (change user_id)
SELECT * FROM accounts WHERE user_id = 1;

-- Get total balance across all accounts
SELECT SUM(balance) as total_bank_balance FROM accounts WHERE is_active = TRUE;

-- Find account by account number
SELECT * FROM accounts WHERE account_number = 'ACC1234567890';

-- View accounts with balance > $1000
SELECT 
    a.account_number,
    u.full_name,
    a.balance
FROM accounts a
JOIN users u ON a.user_id = u.user_id
WHERE a.balance > 1000
ORDER BY a.balance DESC;

-- ==========================================
-- TRANSACTION QUERIES
-- ==========================================

-- View all transactions with account details
SELECT 
    t.transaction_id,
    t.transaction_type,
    t.amount,
    t.transaction_date,
    t.status,
    a1.account_number as from_account,
    a2.account_number as to_account,
    t.description
FROM transactions t
LEFT JOIN accounts a1 ON t.from_account_id = a1.account_id
LEFT JOIN accounts a2 ON t.to_account_id = a2.account_id
ORDER BY t.transaction_date DESC;

-- View recent 10 transactions
SELECT 
    transaction_id,
    transaction_type,
    amount,
    transaction_date,
    status
FROM transactions
ORDER BY transaction_date DESC
LIMIT 10;

-- View transactions by type
SELECT transaction_type, COUNT(*) as count, SUM(amount) as total_amount
FROM transactions
GROUP BY transaction_type;

-- View transactions by status
SELECT status, COUNT(*) as count
FROM transactions
GROUP BY status;

-- View deposits only
SELECT * FROM transactions 
WHERE transaction_type = 'DEPOSIT' 
ORDER BY transaction_date DESC;

-- View transfers only
SELECT * FROM transactions 
WHERE transaction_type = 'TRANSFER' 
ORDER BY transaction_date DESC;

-- Total transaction volume
SELECT 
    COUNT(*) as total_transactions,
    SUM(amount) as total_volume
FROM transactions
WHERE status = 'COMPLETED';

-- Transactions for specific account (by account_id)
SELECT * FROM transactions
WHERE from_account_id = 1 OR to_account_id = 1
ORDER BY transaction_date DESC;

-- ==========================================
-- BANKING SERVICES QUERIES
-- ==========================================

-- View all active services
SELECT * FROM banking_services WHERE is_active = TRUE;

-- View services by type
SELECT * FROM banking_services WHERE service_type = 'LOAN';

-- View customer service applications
SELECT 
    cs.id,
    u.full_name as customer_name,
    bs.service_name,
    cs.amount,
    cs.duration_months,
    cs.status,
    cs.application_date
FROM customer_services cs
JOIN users u ON cs.user_id = u.user_id
JOIN banking_services bs ON cs.service_id = bs.service_id
ORDER BY cs.application_date DESC;

-- Count service applications by status
SELECT status, COUNT(*) as count
FROM customer_services
GROUP BY status;

-- ==========================================
-- STATISTICS QUERIES
-- ==========================================

-- User statistics
SELECT 
    COUNT(*) as total_users,
    SUM(CASE WHEN role = 'ADMIN' THEN 1 ELSE 0 END) as admins,
    SUM(CASE WHEN role = 'CUSTOMER' THEN 1 ELSE 0 END) as customers,
    SUM(CASE WHEN is_active = TRUE THEN 1 ELSE 0 END) as active_users
FROM users;

-- Account statistics
SELECT 
    account_type,
    COUNT(*) as count,
    SUM(balance) as total_balance,
    AVG(balance) as avg_balance,
    MIN(balance) as min_balance,
    MAX(balance) as max_balance
FROM accounts
WHERE is_active = TRUE
GROUP BY account_type;

-- Transaction statistics by date
SELECT 
    DATE(transaction_date) as date,
    COUNT(*) as transaction_count,
    SUM(amount) as total_volume
FROM transactions
WHERE status = 'COMPLETED'
GROUP BY DATE(transaction_date)
ORDER BY date DESC;

-- Top 5 accounts by balance
SELECT 
    a.account_number,
    u.full_name,
    a.account_type,
    a.balance
FROM accounts a
JOIN users u ON a.user_id = u.user_id
ORDER BY a.balance DESC
LIMIT 5;

-- ==========================================
-- DATA MODIFICATION QUERIES (Use Carefully!)
-- ==========================================

-- Reset admin password (to admin123)
UPDATE users SET password = 'admin123' WHERE username = 'admin';

-- Activate/Deactivate user
UPDATE users SET is_active = TRUE WHERE user_id = 1;
UPDATE users SET is_active = FALSE WHERE user_id = 1;

-- Update account balance (for testing - normally done through transactions)
UPDATE accounts SET balance = 5000.00 WHERE account_id = 1;

-- Change transaction status
UPDATE transactions SET status = 'COMPLETED' WHERE transaction_id = 1;

-- ==========================================
-- CLEANUP QUERIES (Use with Caution!)
-- ==========================================

-- Delete all customer data (keep admin)
-- DELETE FROM customer_services WHERE user_id IN (SELECT user_id FROM users WHERE role = 'CUSTOMER');
-- DELETE FROM transactions;
-- DELETE FROM accounts WHERE user_id IN (SELECT user_id FROM users WHERE role = 'CUSTOMER');
-- DELETE FROM users WHERE role = 'CUSTOMER';

-- Reset auto increment
-- ALTER TABLE users AUTO_INCREMENT = 2;
-- ALTER TABLE accounts AUTO_INCREMENT = 1;
-- ALTER TABLE transactions AUTO_INCREMENT = 1;

-- ==========================================
-- SAMPLE DATA INSERTION (For Testing)
-- ==========================================

-- Insert sample customer
INSERT INTO users (username, password, full_name, email, phone, role) 
VALUES ('john_doe', 'password123', 'John Doe', 'john@example.com', '1234567890', 'CUSTOMER');

-- Insert sample account (replace user_id with actual ID)
INSERT INTO accounts (user_id, account_number, account_type, balance) 
VALUES (2, 'ACC1731601234567', 'SAVINGS', 5000.00);

-- Insert sample transaction
INSERT INTO transactions (to_account_id, transaction_type, amount, description, status) 
VALUES (1, 'DEPOSIT', 1000.00, 'Initial deposit', 'COMPLETED');

-- ==========================================
-- USEFUL JOINS
-- ==========================================

-- Complete user profile with accounts and total balance
SELECT 
    u.user_id,
    u.username,
    u.full_name,
    u.email,
    COUNT(a.account_id) as total_accounts,
    COALESCE(SUM(a.balance), 0) as total_balance
FROM users u
LEFT JOIN accounts a ON u.user_id = a.user_id AND a.is_active = TRUE
WHERE u.role = 'CUSTOMER'
GROUP BY u.user_id
ORDER BY total_balance DESC;

-- Account activity summary
SELECT 
    a.account_number,
    u.full_name,
    a.balance,
    COUNT(t.transaction_id) as transaction_count,
    MAX(t.transaction_date) as last_transaction
FROM accounts a
JOIN users u ON a.user_id = u.user_id
LEFT JOIN transactions t ON (t.from_account_id = a.account_id OR t.to_account_id = a.account_id)
GROUP BY a.account_id
ORDER BY transaction_count DESC;

-- ==========================================
-- BACKUP AND RESTORE
-- ==========================================

-- Backup database (Run from command line)
-- mysqldump -u root -p mazebank_db > mazebank_backup.sql

-- Restore database (Run from command line)
-- mysql -u root -p mazebank_db < mazebank_backup.sql

-- ==========================================
-- END OF QUERIES
-- ==========================================

-- Quick health check
SELECT 
    'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Accounts', COUNT(*) FROM accounts
UNION ALL
SELECT 'Transactions', COUNT(*) FROM transactions
UNION ALL
SELECT 'Banking Services', COUNT(*) FROM banking_services
UNION ALL
SELECT 'Customer Services', COUNT(*) FROM customer_services;
