package com.mazebank.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Account Model Class
 * Represents a bank account in the system
 */
public class Account {
    private int accountId;
    private int userId;
    private String accountNumber;
    private String accountType;  // SAVINGS, CURRENT, FIXED_DEPOSIT
    private BigDecimal balance;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isActive;
    
    // Constructors
    public Account() {}
    
    public Account(int userId, String accountNumber, String accountType, BigDecimal balance) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }
    
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    /**
     * Base hook for polymorphic monthly fee calculation.
     * Subclasses may override; default is zero.
     */
    public java.math.BigDecimal calculateMonthlyFee() {
        return java.math.BigDecimal.ZERO;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                '}';
    }
}
