package com.mazebank.dao;

import com.mazebank.model.BankingService;

/**
 * DAO interface for BankingService operations
 */
public interface IBankingServiceDAO {
    java.util.List<BankingService> getAllServices();
    java.util.List<BankingService> getServicesByType(String serviceType);
    BankingService getServiceById(int serviceId);
    boolean applyForService(int userId, int serviceId, double amount, int durationMonths);
    java.util.List<String> getUserServices(int userId);
    java.util.List<Object[]> getPendingApplications();
    boolean updateApplicationStatus(int applicationId, String status);
}
