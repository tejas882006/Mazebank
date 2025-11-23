package com.mazebank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Utility Class
 * Manages MySQL database connections using JDBC
 */
public class DatabaseConnection {
    
    // Database credentials - CHANGE THESE ACCORDING TO YOUR MYSQL SETUP
    private static final String URL = "jdbc:mysql://localhost:3306/mazebank_db?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";  // Change to your MySQL username
    private static final String PASSWORD = "Nirvan@889";      // Change to your MySQL password
    
    private static Connection connection = null;
    
    /**
     * Get database connection
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Establish connection
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
        return connection;
    }
    
    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Diagnostic entry point to help troubleshoot connection issues.
     * Prints detailed status and attempts a simple query if connected.
     */
    public static void main(String[] args) {
        System.out.println("--- MazeBank DB Connection Diagnostic ---");
        System.out.println("JDBC URL      : " + URL);
        System.out.println("Username      : " + USERNAME);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Load   : SUCCESS");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver Load   : FAILED - " + e.getMessage());
            e.printStackTrace();
            return;
        }

        Connection conn = getConnection();
        if (conn == null) {
            System.out.println("Connection    : FAILED (null returned)");
            return;
        }
        try {
            if (conn.isClosed()) {
                System.out.println("Connection    : FAILED (closed)");
                return;
            } else {
                System.out.println("Connection    : SUCCESS");
            }
            // Optional simple validation query
            try (var stmt = conn.createStatement()) {
                stmt.execute("SELECT 1");
                System.out.println("Validation Qry: SUCCESS (SELECT 1)");
            } catch (SQLException e) {
                System.out.println("Validation Qry: FAILED - " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Connection    : ERROR - " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        System.out.println("--- End Diagnostic ---");
    }
}
