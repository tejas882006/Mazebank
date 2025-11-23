package com.mazebank.dao;

import com.mazebank.model.Transaction;

import java.util.List;
import java.math.BigDecimal;

/**
 * DAO interface for Transaction operations
 */
public interface ITransactionDAO {
    boolean createTransaction(Transaction transaction);
    List<Transaction> getTransactionsByAccountId(int accountId);
    boolean deleteTransactionsByAccountId(int accountId);
    List<Transaction> getAllTransactions();
    List<Transaction> getRecentTransactions(int accountId, int limit);
    BigDecimal getTotalDeposits(int accountId);
    BigDecimal getTotalWithdrawals(int accountId);
}
