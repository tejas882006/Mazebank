package com.mazebank.dao;

import com.mazebank.model.Transaction;
import com.mazebank.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction Data Access Object
 * Handles all database operations for Transaction entity
 */
public class TransactionDAO implements ITransactionDAO {
    
    /**
     * Create new transaction
     */
    public boolean createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (from_account_id, to_account_id, transaction_type, amount, description, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (transaction.getFromAccountId() != null) {
                pstmt.setInt(1, transaction.getFromAccountId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            
            if (transaction.getToAccountId() != null) {
                pstmt.setInt(2, transaction.getToAccountId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            pstmt.setString(3, transaction.getTransactionType());
            pstmt.setBigDecimal(4, transaction.getAmount());
            pstmt.setString(5, transaction.getDescription());
            pstmt.setString(6, transaction.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get transactions by account ID
     */
    public List<Transaction> getTransactionsByAccountId(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, " +
                    "a1.account_number as from_account_number, " +
                    "a2.account_number as to_account_number " +
                    "FROM transactions t " +
                    "LEFT JOIN accounts a1 ON t.from_account_id = a1.account_id " +
                    "LEFT JOIN accounts a2 ON t.to_account_id = a2.account_id " +
                    "WHERE t.from_account_id = ? OR t.to_account_id = ? " +
                    "ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            pstmt.setInt(2, accountId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
    /**
     * Delete all transactions for an account
     */
    public boolean deleteTransactionsByAccountId(int accountId) {
        String sql = "DELETE FROM transactions WHERE from_account_id = ? OR to_account_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            pstmt.setInt(2, accountId);
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all transactions (for admin)
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, " +
                    "a1.account_number as from_account_number, " +
                    "a2.account_number as to_account_number " +
                    "FROM transactions t " +
                    "LEFT JOIN accounts a1 ON t.from_account_id = a1.account_id " +
                    "LEFT JOIN accounts a2 ON t.to_account_id = a2.account_id " +
                    "ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
    /**
     * Get recent transactions (limit)
     */
    public List<Transaction> getRecentTransactions(int accountId, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, " +
                    "a1.account_number as from_account_number, " +
                    "a2.account_number as to_account_number " +
                    "FROM transactions t " +
                    "LEFT JOIN accounts a1 ON t.from_account_id = a1.account_id " +
                    "LEFT JOIN accounts a2 ON t.to_account_id = a2.account_id " +
                    "WHERE t.from_account_id = ? OR t.to_account_id = ? " +
                    "ORDER BY t.transaction_date DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            pstmt.setInt(2, accountId);
            pstmt.setInt(3, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
    /**
     * Get transaction statistics
     */
    public BigDecimal getTotalDeposits(int accountId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM transactions " +
                    "WHERE to_account_id = ? AND transaction_type = 'DEPOSIT' AND status = 'COMPLETED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalWithdrawals(int accountId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM transactions " +
                    "WHERE from_account_id = ? AND transaction_type = 'WITHDRAWAL' AND status = 'COMPLETED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Extract Transaction object from ResultSet
     */
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        
        int fromAccountId = rs.getInt("from_account_id");
        transaction.setFromAccountId(rs.wasNull() ? null : fromAccountId);
        
        int toAccountId = rs.getInt("to_account_id");
        transaction.setToAccountId(rs.wasNull() ? null : toAccountId);
        
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setDescription(rs.getString("description"));
        transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
        transaction.setStatus(rs.getString("status"));
        
        transaction.setFromAccountNumber(rs.getString("from_account_number"));
        transaction.setToAccountNumber(rs.getString("to_account_number"));
        
        return transaction;
    }
}
