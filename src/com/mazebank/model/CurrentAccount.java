package com.mazebank.model;

import java.math.BigDecimal;

/**
 * CurrentAccount demonstrates inheritance with an overdraft feature
 * and a monthly maintenance fee.
 */
public class CurrentAccount extends Account {
    private BigDecimal overdraftLimit = new BigDecimal("500.00");
    private BigDecimal monthlyFee = new BigDecimal("10.00");

    public CurrentAccount() {
        setAccountType("CURRENT");
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(BigDecimal monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    @Override
    public BigDecimal calculateMonthlyFee() {
        return monthlyFee == null ? BigDecimal.ZERO : monthlyFee;
    }
}
