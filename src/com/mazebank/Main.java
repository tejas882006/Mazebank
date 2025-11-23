package com.mazebank;

import com.mazebank.ui.LoginFrame;
import com.mazebank.util.DatabaseConnection;

import javax.swing.*;

/**
 * Main Application Entry Point
 * MazeBank Online Banking System
 */
public class Main {
    
    public static void main(String[] args) {
        // Set Look and Feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Test database connection
        if (DatabaseConnection.testConnection()) {
            System.out.println("===========================================");
            System.out.println("  MazeBank Online Banking System Started  ");
            System.out.println("===========================================");
            
            // Launch Login Frame
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(null,
                "Failed to connect to database.\n\n" +
                "Please ensure:\n" +
                "1. MySQL server is running\n" +
                "2. Database 'mazebank_db' exists\n" +
                "3. Database credentials are correct in DatabaseConnection.java\n" +
                "4. MySQL JDBC connector is in the lib folder",
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
