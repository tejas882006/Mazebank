package com.mazebank.model;

import java.math.BigDecimal;

/**
 * Banking Service Model Class
 * Represents banking services like loans, investments, etc.
 */
public class BankingService {
    private int serviceId;
    private String serviceName;
    private String serviceType;  // LOAN, INVESTMENT, INSURANCE, CREDIT_CARD
    private String description;
    private BigDecimal interestRate;
    private boolean isActive;
    
    // Constructors
    public BankingService() {}
    
    public BankingService(String serviceName, String serviceType, String description, BigDecimal interestRate) {
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.description = description;
        this.interestRate = interestRate;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getServiceType() {
        return serviceType;
    }
    
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    @Override
    public String toString() {
        return "BankingService{" +
                "serviceId=" + serviceId +
                ", serviceName='" + serviceName + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", interestRate=" + interestRate +
                '}';
    }
}
