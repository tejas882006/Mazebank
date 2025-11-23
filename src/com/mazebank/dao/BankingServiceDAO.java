package com.mazebank.dao;

import com.mazebank.model.BankingService;
import com.mazebank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Banking Service Data Access Object
 * Handles all database operations for Banking Service entity
 */
public class BankingServiceDAO implements IBankingServiceDAO {
    
    /**
     * Get all active banking services
     */
    public List<BankingService> getAllServices() {
        List<BankingService> services = new ArrayList<>();
        String sql = "SELECT * FROM banking_services WHERE is_active = TRUE ORDER BY service_type, service_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                services.add(extractServiceFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }
    
    /**
     * Get services by type
     */
    public List<BankingService> getServicesByType(String serviceType) {
        List<BankingService> services = new ArrayList<>();
        String sql = "SELECT * FROM banking_services WHERE service_type = ? AND is_active = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, serviceType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                services.add(extractServiceFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }
    
    /**
     * Get service by ID
     */
    public BankingService getServiceById(int serviceId) {
        String sql = "SELECT * FROM banking_services WHERE service_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractServiceFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Apply for a service
     */
    public boolean applyForService(int userId, int serviceId, double amount, int durationMonths) {
        String sql = "INSERT INTO customer_services (user_id, service_id, amount, duration_months) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, serviceId);
            pstmt.setDouble(3, amount);
            pstmt.setInt(4, durationMonths);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get user's applied services
     */
    public List<String> getUserServices(int userId) {
        List<String> services = new ArrayList<>();
        String sql = "SELECT bs.service_name, cs.status, cs.amount, cs.application_date " +
                    "FROM customer_services cs " +
                    "JOIN banking_services bs ON cs.service_id = bs.service_id " +
                    "WHERE cs.user_id = ? ORDER BY cs.application_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String service = rs.getString("service_name") + " - $" + 
                               rs.getDouble("amount") + " (" + 
                               rs.getString("status") + ")";
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }
    
    /**
     * Get all pending service applications (for admin)
     */
    public List<Object[]> getPendingApplications() {
        List<Object[]> applications = new ArrayList<>();
        String sql = "SELECT cs.id, cs.user_id, u.full_name, u.username, " +
                    "bs.service_name, cs.amount, cs.duration_months, " +
                    "cs.application_date, cs.status " +
                    "FROM customer_services cs " +
                    "JOIN users u ON cs.user_id = u.user_id " +
                    "JOIN banking_services bs ON cs.service_id = bs.service_id " +
                    "ORDER BY cs.application_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] app = new Object[9];
                app[0] = rs.getInt("id");
                app[1] = rs.getInt("user_id");
                app[2] = rs.getString("full_name");
                app[3] = rs.getString("username");
                app[4] = rs.getString("service_name");
                app[5] = rs.getDouble("amount");
                app[6] = rs.getInt("duration_months");
                app[7] = rs.getTimestamp("application_date");
                app[8] = rs.getString("status");
                applications.add(app);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }
    
    /**
     * Approve or reject service application
     */
    public boolean updateApplicationStatus(int applicationId, String status) {
        String sql = "UPDATE customer_services SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, applicationId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Extract BankingService object from ResultSet
     */
    private BankingService extractServiceFromResultSet(ResultSet rs) throws SQLException {
        BankingService service = new BankingService();
        service.setServiceId(rs.getInt("service_id"));
        service.setServiceName(rs.getString("service_name"));
        service.setServiceType(rs.getString("service_type"));
        service.setDescription(rs.getString("description"));
        service.setInterestRate(rs.getBigDecimal("interest_rate"));
        service.setActive(rs.getBoolean("is_active"));
        return service;
    }
}
