package com.mazebank.dao;

import com.mazebank.model.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * DAO interface for Account operations
 */
public interface IAccountDAO {
    boolean createAccount(Account account);
    List<Account> getAccountsByUserId(int userId);
    Account getAccountByNumber(String accountNumber);
    Account getAccountById(int accountId);
    boolean updateBalance(int accountId, BigDecimal newBalance);
    java.math.BigDecimal getTotalBalance(int userId);
    String generateAccountNumber();
    boolean deleteAccount(int accountId);
    boolean accountNumberExists(String accountNumber);
    List<Account> getAllAccounts();
}
