package com.mazebank.service;

import com.mazebank.exceptions.InsufficientFundsException;
import com.mazebank.exceptions.InvalidTransactionException;
import com.mazebank.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TransactionService coordinates atomic account transfers with JDBC transactions
 * and demonstrates synchronization to protect concurrent updates.
 */
public class TransactionService {
    // Per-account locks to avoid race conditions and demonstrate synchronization
    private static final Map<Integer, ReentrantLock> accountLocks = new ConcurrentHashMap<>();

    private static Lock lockFor(int accountId) {
        return accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
    }

    /**
     * Transfer amount from one account to another atomically.
     * Uses JDBC transaction and consistent lock ordering to prevent deadlocks.
     */
    public void transfer(int fromAccountId, int toAccountId, BigDecimal amount, String description)
            throws InvalidTransactionException, InsufficientFundsException, SQLException {
        if (fromAccountId == toAccountId) {
            throw new InvalidTransactionException("Source and destination accounts must differ");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidTransactionException("Amount must be positive");
        }

        int first = Math.min(fromAccountId, toAccountId);
        int second = Math.max(fromAccountId, toAccountId);
        Lock firstLock = lockFor(first);
        Lock secondLock = lockFor(second);

        firstLock.lock();
        try {
            secondLock.lock();
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    BigDecimal fromBalance = getBalance(conn, fromAccountId);
                    if (fromBalance == null) throw new InvalidTransactionException("From account not found");
                    BigDecimal toBalance = getBalance(conn, toAccountId);
                    if (toBalance == null) throw new InvalidTransactionException("To account not found");

                    if (fromBalance.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
                        throw new InsufficientFundsException("Insufficient funds for transfer");
                    }

                    updateBalance(conn, fromAccountId, fromBalance.subtract(amount));
                    updateBalance(conn, toAccountId, toBalance.add(amount));
                    insertTransaction(conn, fromAccountId, toAccountId, amount, description);

                    conn.commit();
                } catch (Exception ex) {
                    conn.rollback();
                    if (ex instanceof InvalidTransactionException) throw (InvalidTransactionException) ex;
                    if (ex instanceof InsufficientFundsException) throw (InsufficientFundsException) ex;
                    if (ex instanceof SQLException) throw (SQLException) ex;
                    throw new RuntimeException(ex);
                } finally {
                    conn.setAutoCommit(true);
                }
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

    private BigDecimal getBalance(Connection conn, int accountId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT balance FROM accounts WHERE account_id = ? AND is_active = TRUE")) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
                return null;
            }
        }
    }

    private void updateBalance(Connection conn, int accountId, BigDecimal newBalance) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE accounts SET balance = ?, updated_at = CURRENT_TIMESTAMP WHERE account_id = ?")) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    private void insertTransaction(Connection conn, int fromAccountId, int toAccountId, BigDecimal amount, String description) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO transactions (from_account_id, to_account_id, transaction_type, amount, description, status) VALUES (?, ?, 'TRANSFER', ?, ?, 'COMPLETED')")) {
            ps.setInt(1, fromAccountId);
            ps.setInt(2, toAccountId);
            ps.setBigDecimal(3, amount);
            ps.setString(4, description == null ? "" : description);
            ps.executeUpdate();
        }
    }
}
