package com.mazebank.ui;

import com.mazebank.dao.AccountDAO;
import com.mazebank.dao.UserDAO;
import com.mazebank.model.Account;
import com.mazebank.model.User;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Registration Frame
 * Professional UI for new customer registration
 */
public class RegistrationFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> accountTypeCombo;
    private JTextField initialDepositField;
    private JButton registerButton;
    private JButton backButton;
    
    private UserDAO userDAO;
    private AccountDAO accountDAO;
    
    public RegistrationFrame() {
        userDAO = new UserDAO();
        accountDAO = new AccountDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("MazeBank - Customer Registration");
        setSize(550, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(41, 128, 185));
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(550, 70));
        
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Form Panel with ScrollPane
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridx = 0;
        
        int row = 0;
        
        // Full Name
        addFormField(formPanel, gbc, row++, "Full Name:");
        fullNameField = createTextField();
        gbc.gridy = row++;
        formPanel.add(fullNameField, gbc);
        
        // Username
        addFormField(formPanel, gbc, row++, "Username:");
        usernameField = createTextField();
        gbc.gridy = row++;
        formPanel.add(usernameField, gbc);
        
        // Email
        addFormField(formPanel, gbc, row++, "Email:");
        emailField = createTextField();
        gbc.gridy = row++;
        formPanel.add(emailField, gbc);
        
        // Phone
        addFormField(formPanel, gbc, row++, "Phone Number:");
        phoneField = createTextField();
        gbc.gridy = row++;
        formPanel.add(phoneField, gbc);
        
        // Password
        addFormField(formPanel, gbc, row++, "Password:");
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(350, 35));
        styleField(passwordField);
        gbc.gridy = row++;
        formPanel.add(passwordField, gbc);
        
        // Confirm Password
        addFormField(formPanel, gbc, row++, "Confirm Password:");
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField.setPreferredSize(new Dimension(350, 35));
        styleField(confirmPasswordField);
        gbc.gridy = row++;
        formPanel.add(confirmPasswordField, gbc);
        
        // Account Type
        addFormField(formPanel, gbc, row++, "Account Type:");
        String[] accountTypes = {"SAVINGS", "CURRENT"};
        accountTypeCombo = new JComboBox<>(accountTypes);
        accountTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        accountTypeCombo.setPreferredSize(new Dimension(350, 35));
        gbc.gridy = row++;
        formPanel.add(accountTypeCombo, gbc);
        
        // Initial Deposit
        addFormField(formPanel, gbc, row++, "Initial Deposit (Min $500):");
        initialDepositField = createTextField();
        gbc.gridy = row++;
        formPanel.add(initialDepositField, gbc);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonsPanel.setBackground(Color.WHITE);
        
        registerButton = createStyledButton("Register", new Color(46, 204, 113));
        backButton = createStyledButton("Back to Login", new Color(149, 165, 166));
        
        buttonsPanel.add(registerButton);
        buttonsPanel.add(backButton);
        
        gbc.gridy = row++;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(buttonsPanel, gbc);
        
        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Action Listeners
        registerButton.addActionListener(e -> handleRegistration());
        backButton.addActionListener(e -> backToLogin());
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(350, 35));
        styleField(field);
        return field;
    }
    
    private void styleField(JComponent field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = row;
        panel.add(label, gbc);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void handleRegistration() {
        // Validate inputs
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String accountType = (String) accountTypeCombo.getSelectedItem();
        String depositStr = initialDepositField.getText().trim();
        
        // Validation
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || 
            password.isEmpty() || depositStr.isEmpty()) {
            showError("Please fill in all required fields.");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long.");
            return;
        }
        
        if (userDAO.usernameExists(username)) {
            showError("Username already exists. Please choose another.");
            return;
        }
        
        if (userDAO.emailExists(email)) {
            showError("Email already registered.");
            return;
        }
        
        double deposit;
        try {
            deposit = Double.parseDouble(depositStr);
            if (deposit < 500) {
                showError("Initial deposit must be at least $500.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Invalid deposit amount.");
            return;
        }
        
        // Create user
        User user = new User(username, password, fullName, email, "CUSTOMER");
        user.setPhone(phone);
        
        if (userDAO.registerUser(user)) {
            // Get the created user to get the user ID
            User createdUser = userDAO.login(username, password);
            
            if (createdUser != null) {
                // Create account
                String accountNumber = accountDAO.generateAccountNumber();
                Account account = new Account(
                    createdUser.getUserId(),
                    accountNumber,
                    accountType,
                    BigDecimal.valueOf(deposit)
                );
                
                System.out.println("Creating account for user ID: " + createdUser.getUserId());
                System.out.println("Account Number: " + accountNumber);
                System.out.println("Account Type: " + accountType);
                System.out.println("Initial Balance: " + deposit);
                
                boolean accountCreated = accountDAO.createAccount(account);
                System.out.println("Account creation result: " + accountCreated);
                
                if (accountCreated) {
                    JOptionPane.showMessageDialog(this,
                        "Registration successful!\n\n" +
                        "Your Account Number: " + accountNumber + "\n" +
                        "Account Type: " + accountType + "\n" +
                        "Initial Balance: $" + deposit + "\n\n" +
                        "You can now login with your credentials.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    backToLogin();
                } else {
                    System.err.println("ERROR: Account creation failed!");
                    showError("Failed to create account. Please contact support.");
                }
            } else {
                System.err.println("ERROR: Could not retrieve created user!");
                showError("User created but login failed. Please try logging in.");
            }
        } else {
            showError("Registration failed. Please try again.");
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Registration Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void backToLogin() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}
