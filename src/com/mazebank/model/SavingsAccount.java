package com.mazebank.model;

import java.math.BigDecimal;

/**
 * SavingsAccount demonstrates inheritance and polymorphism.
 */
public class SavingsAccount extends Account {
    private BigDecimal interestRate = new BigDecimal("0.02"); // 2% annual by default

    public SavingsAccount() {
        setAccountType("SAVINGS");
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * Apply monthly interest to balance (simplified).
     */
    public void applyMonthlyInterest() {
        if (getBalance() == null || interestRate == null) return;
        BigDecimal monthlyRate = interestRate.divide(new BigDecimal("12"), java.math.RoundingMode.HALF_UP);
        setBalance(getBalance().add(getBalance().multiply(monthlyRate)));
    }

    @Override
    public BigDecimal calculateMonthlyFee() {
        // No monthly fee for savings
        return BigDecimal.ZERO;
    }
}
