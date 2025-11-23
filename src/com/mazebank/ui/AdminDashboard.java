package com.mazebank.ui;

import com.mazebank.dao.*;
import com.mazebank.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Admin Dashboard
 * Main interface for admin operations
 */
public class AdminDashboard extends JFrame {
    
    private User currentAdmin;
    private UserDAO userDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private BankingServiceDAO serviceDAO;
    
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    
    public AdminDashboard(User admin) {
        this.currentAdmin = admin;
        this.userDAO = new UserDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        this.serviceDAO = new BankingServiceDAO();
        
        this.currencyFormat = NumberFormat.getCurrencyInstance();
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("MazeBank - Admin Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main Layout
        setLayout(new BorderLayout());
        
        // Top Panel (Header)
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Left Panel (Navigation)
        JPanel leftPanel = createNavigationPanel();
        add(leftPanel, BorderLayout.WEST);
        
        // Center Panel (Main Content)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Color.WHITE);
        
        // Add different views
        mainContentPanel.add(createDashboardPanel(), "DASHBOARD");
        mainContentPanel.add(createUserManagementPanel(), "USERS");
        mainContentPanel.add(createTransactionMonitoringPanel(), "TRANSACTIONS");
        mainContentPanel.add(createServiceApprovalsPanel(), "APPROVALS");
        mainContentPanel.add(createSystemSettingsPanel(), "SETTINGS");
        
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Show dashboard by default
        cardLayout.show(mainContentPanel, "DASHBOARD");
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setPreferredSize(new Dimension(1200, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Left side - Bank name
        JLabel bankLabel = new JLabel("MazeBank - Admin Panel");
        bankLabel.setFont(new Font("Arial", Font.BOLD, 28));
        bankLabel.setForeground(Color.WHITE);
        
        // Right side - Admin info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JLabel adminLabel = new JLabel("Admin: " + currentAdmin.getFullName());
        adminLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        adminLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());
        
        rightPanel.add(adminLabel);
        rightPanel.add(logoutButton);
        
        panel.add(bankLabel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(44, 62, 80));
        panel.setPreferredSize(new Dimension(220, 600));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Navigation buttons
        String[] navItems = {
            "Dashboard",
            "User Management",
            "Transaction Monitor",
            "Service Approvals",
            "System Settings"
        };
        
        String[] navCommands = {
            "DASHBOARD",
            "USERS",
            "TRANSACTIONS",
            "APPROVALS",
            "SETTINGS"
        };
        
        for (int i = 0; i < navItems.length; i++) {
            JButton button = createNavButton(navItems[i], navCommands[i]);
            panel.add(button);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        return panel;
    }
    
    private JButton createNavButton(String text, String command) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setMaximumSize(new Dimension(200, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            cardLayout.show(mainContentPanel, command);
            refreshContent(command);
        });
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 73, 94));
            }
        });
        
        return button;
    }
    
    // DASHBOARD PANEL
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with title and admin name
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel adminNameLabel = new JLabel("Welcome, " + currentAdmin.getFullName());
        adminNameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        adminNameLabel.setForeground(new Color(52, 73, 94));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(adminNameLabel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Statistics Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Total Users
        List<User> allUsers = userDAO.getAllUsers();
        long customerCount = allUsers.stream().filter(u -> "CUSTOMER".equals(u.getRole())).count();
        JPanel usersCard = createStatCard("Total Customers", String.valueOf(customerCount), new Color(52, 152, 219));
        statsPanel.add(usersCard);
        
        // Total Accounts
        List<Account> allAccounts = accountDAO.getAllAccounts();
        JPanel accountsCard = createStatCard("Total Accounts", String.valueOf(allAccounts.size()), new Color(46, 204, 113));
        statsPanel.add(accountsCard);
        
        // Total Transactions
        List<Transaction> allTransactions = transactionDAO.getAllTransactions();
        JPanel transactionsCard = createStatCard("Total Transactions", String.valueOf(allTransactions.size()), new Color(155, 89, 182));
        statsPanel.add(transactionsCard);
        
        // Active Users
        long activeUsers = allUsers.stream().filter(User::isActive).count();
        JPanel activeCard = createStatCard("Active Users", String.valueOf(activeUsers), new Color(230, 126, 34));
        statsPanel.add(activeCard);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Bottom section with user details and recent transactions
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        bottomPanel.setBackground(Color.WHITE);
        
        // User Details Panel
        JPanel userDetailsPanel = new JPanel(new BorderLayout());
        userDetailsPanel.setBackground(Color.WHITE);
        userDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("User Details"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        String[] userColumns = {"User ID", "Username", "Full Name", "Role"};
        DefaultTableModel userModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable userTable = new JTable(userModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        userTable.getTableHeader().setBackground(new Color(236, 240, 241));
        
        // Load all users (scroll pane will handle overflow)
        for (User u : allUsers) {
            userModel.addRow(new Object[]{
                String.format("%03d", u.getUserId()), // 3-digit format
                u.getUsername(),
                u.getFullName(),
                u.getRole()
            });
        }
        
        JScrollPane userScrollPane = new JScrollPane(userTable);
        userDetailsPanel.add(userScrollPane, BorderLayout.CENTER);
        
        bottomPanel.add(userDetailsPanel);
        
        // Recent Activity
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(Color.WHITE);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Recent Transactions"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        String[] columns = {"ID", "Date", "Type", "Amount", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        
        // Load recent transactions (max 10)
        int count = 0;
        for (Transaction trans : allTransactions) {
            if (count >= 10) break;
            model.addRow(new Object[]{
                trans.getTransactionId(),
                dateFormat.format(trans.getTransactionDate()),
                trans.getTransactionType(),
                currencyFormat.format(trans.getAmount()),
                trans.getStatus()
            });
            count++;
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        activityPanel.add(scrollPane, BorderLayout.CENTER);
        
        bottomPanel.add(activityPanel);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(valueLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    // USER MANAGEMENT PANEL
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with title and add button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JButton addUserButton = new JButton("Add New User");
        addUserButton.setBackground(new Color(46, 204, 113));
        addUserButton.setForeground(Color.WHITE);
        addUserButton.setFocusPainted(false);
        addUserButton.setBorderPainted(false);
        addUserButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addUserButton.addActionListener(e -> showAddUserDialog());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addUserButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Users Table
        String[] columns = {"User ID", "Username", "Full Name", "Email", "Phone", "Role", "Account Number", "Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only Actions column
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Set column widths for proper alignment
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // User ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Username
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Full Name
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Phone
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Role
        table.getColumnModel().getColumn(6).setPreferredWidth(180); // Account Number
        table.getColumnModel().getColumn(7).setPreferredWidth(80);  // Status
        table.getColumnModel().getColumn(8).setPreferredWidth(150); // Actions
        
        // Add Edit and Delete buttons to each row
        table.getColumn("Actions").setCellRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
            
            JButton editBtn = new JButton("Edit");
            editBtn.setFont(new Font("Arial", Font.PLAIN, 10));
            editBtn.setBackground(new Color(52, 152, 219));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFocusPainted(false);
            editBtn.setBorderPainted(false);
            
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.setFont(new Font("Arial", Font.PLAIN, 10));
            deleteBtn.setBackground(new Color(231, 76, 60));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);
            
            panel1.add(editBtn);
            panel1.add(deleteBtn);
            
            return panel1;
        });
        
        table.getColumn("Actions").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
                
                JButton editBtn = new JButton("Edit");
                editBtn.setFont(new Font("Arial", Font.PLAIN, 10));
                editBtn.setBackground(new Color(52, 152, 219));
                editBtn.setForeground(Color.WHITE);
                editBtn.setFocusPainted(false);
                editBtn.setBorderPainted(false);
                editBtn.addActionListener(e -> editUser(table, row));
                
                JButton deleteBtn = new JButton("Delete");
                deleteBtn.setFont(new Font("Arial", Font.PLAIN, 10));
                deleteBtn.setBackground(new Color(231, 76, 60));
                deleteBtn.setForeground(Color.WHITE);
                deleteBtn.setFocusPainted(false);
                deleteBtn.setBorderPainted(false);
                deleteBtn.addActionListener(e -> deleteUser(table, row));
                
                panel1.add(editBtn);
                panel1.add(deleteBtn);
                
                return panel1;
            }
        });
        
        loadUsers(model);
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadUsers(DefaultTableModel model) {
        model.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        
        for (User user : users) {
            // Get user's account number if they have one
            String accountNumber = "N/A";
            if ("CUSTOMER".equals(user.getRole())) {
                List<Account> userAccounts = accountDAO.getAccountsByUserId(user.getUserId());
                if (!userAccounts.isEmpty()) {
                    accountNumber = userAccounts.get(0).getAccountNumber();
                    // If multiple accounts, show first one with count
                    if (userAccounts.size() > 1) {
                        accountNumber += " (+" + (userAccounts.size() - 1) + " more)";
                    }
                }
            }
            
            model.addRow(new Object[]{
                String.format("%03d", user.getUserId()), // 3-digit format
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone() != null ? user.getPhone() : "N/A",
                user.getRole(),
                accountNumber,
                user.isActive() ? "Active" : "Inactive",
                "" // Actions column
            });
        }
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Form fields
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField fullNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"CUSTOMER", "ADMIN"});
        JComboBox<String> accountTypeCombo = new JComboBox<>(new String[]{"SAVINGS", "CHECKING", "BUSINESS"});
        JTextField initialBalanceField = new JTextField(20);
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleCombo, gbc);
        
        // Account fields (only for CUSTOMER role)
        JLabel accountTypeLabel = new JLabel("Account Type:");
        JLabel initialBalanceLabel = new JLabel("Initial Balance (Min $500):");
        
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(accountTypeLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(accountTypeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(initialBalanceLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(initialBalanceField, gbc);
        
        // Toggle account fields visibility based on role
        roleCombo.addActionListener(e -> {
            boolean isCustomer = "CUSTOMER".equals(roleCombo.getSelectedItem());
            accountTypeLabel.setVisible(isCustomer);
            accountTypeCombo.setVisible(isCustomer);
            initialBalanceLabel.setVisible(isCustomer);
            initialBalanceField.setVisible(isCustomer);
        });
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            
            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all required fields.");
                return;
            }
            
            // Validate initial balance for CUSTOMER
            BigDecimal initialBalance = BigDecimal.ZERO;
            if ("CUSTOMER".equals(role)) {
                String balanceStr = initialBalanceField.getText().trim();
                if (balanceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter initial balance for customer account.");
                    return;
                }
                try {
                    initialBalance = new BigDecimal(balanceStr);
                    if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
                        JOptionPane.showMessageDialog(dialog, "Initial balance cannot be negative.");
                        return;
                    }
                    if (initialBalance.compareTo(new BigDecimal("500")) < 0) {
                        JOptionPane.showMessageDialog(dialog, "Initial balance must be at least $500.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid balance amount.");
                    return;
                }
            }
            
            User user = new User(username, password, fullName, email, role);
            user.setPhone(phone);
            
            if (userDAO.registerUser(user)) {
                // If customer, create account
                if ("CUSTOMER".equals(role)) {
                    User createdUser = userDAO.login(username, password);
                    if (createdUser != null) {
                        String accountNumber = accountDAO.generateAccountNumber();
                        String accountType = (String) accountTypeCombo.getSelectedItem();
                        Account account = new Account(
                            createdUser.getUserId(),
                            accountNumber,
                            accountType,
                            initialBalance
                        );
                        
                        if (accountDAO.createAccount(account)) {
                            JOptionPane.showMessageDialog(dialog, 
                                "User and account created successfully!\n\n" +
                                "Account Number: " + accountNumber + "\n" +
                                "Account Type: " + accountType + "\n" +
                                "Initial Balance: $" + initialBalance);
                        } else {
                            JOptionPane.showMessageDialog(dialog, 
                                "User created but account creation failed.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "User added successfully!");
                }
                dialog.dispose();
                refreshContent("USERS");
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add user.");
            }
        });
        
        gbc.gridx = 1; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(saveButton, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void editUser(JTable table, int row) {
        // Parse the 3-digit user ID string back to int
        String userIdStr = (String) table.getValueAt(row, 0);
        int userId = Integer.parseInt(userIdStr);
        User user = userDAO.getUserById(userId);
        
        if (user == null) return;
        
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField usernameField = new JTextField(user.getUsername(), 20);
        JTextField fullNameField = new JTextField(user.getFullName(), 20);
        JTextField emailField = new JTextField(user.getEmail(), 20);
        JTextField phoneField = new JTextField(user.getPhone() != null ? user.getPhone() : "", 20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"CUSTOMER", "ADMIN"});
        roleCombo.setSelectedItem(user.getRole());
        
        int r = 0;
        
        gbc.gridx = 0; gbc.gridy = r++;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = r++;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = r++;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = r++;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = r++;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleCombo, gbc);
        
        JButton updateButton = new JButton("Update");
        updateButton.setBackground(new Color(52, 152, 219));
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(e -> {
            user.setUsername(usernameField.getText().trim());
            user.setFullName(fullNameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setPhone(phoneField.getText().trim());
            user.setRole((String) roleCombo.getSelectedItem());
            
            if (userDAO.updateUser(user)) {
                JOptionPane.showMessageDialog(dialog, "User updated successfully!");
                dialog.dispose();
                refreshContent("USERS");
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update user.");
            }
        });
        
        gbc.gridx = 1; gbc.gridy = r;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(updateButton, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void deleteUser(JTable table, int row) {
        // Parse the 3-digit user ID string back to int
        String userIdStr = (String) table.getValueAt(row, 0);
        int userId = Integer.parseInt(userIdStr);
        String username = (String) table.getValueAt(row, 1);
        String fullName = (String) table.getValueAt(row, 2);
        
        // Don't allow deleting yourself
        if (userId == currentAdmin.getUserId()) {
            JOptionPane.showMessageDialog(this, 
                "You cannot delete your own account!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to PERMANENTLY delete user '" + username + "' (" + fullName + ")?\n\n" +
            "This will delete:\n" +
            "- User account\n" +
            "- All bank accounts\n" +
            "- All transactions\n" +
            "- All service applications\n\n" +
            "This action CANNOT be undone!",
            "Confirm Permanent Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Get user's accounts first
                List<Account> userAccounts = accountDAO.getAccountsByUserId(userId);
                
                // Delete all transactions for each account
                for (Account account : userAccounts) {
                    transactionDAO.deleteTransactionsByAccountId(account.getAccountId());
                }
                
                // Delete all accounts
                for (Account account : userAccounts) {
                    accountDAO.deleteAccount(account.getAccountId());
                }
                
                // Delete service applications
                // Note: If you have a service applications table, delete those here
                
                // Finally, delete the user
                if (userDAO.deleteUserPermanently(userId)) {
                    JOptionPane.showMessageDialog(this, 
                        "User '" + username + "' and all related data deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshContent("USERS");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete user.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error deleting user: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // TRANSACTION MONITORING PANEL
    private JPanel createTransactionMonitoringPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Transaction Monitoring");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Transactions Table
        String[] columns = {"Trans ID", "Date", "User Name", "Type", "From Account", "To Account", "Amount", "Status", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        
        loadAllTransactions(model);
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Statistics Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        List<Transaction> allTrans = transactionDAO.getAllTransactions();
        long completedCount = allTrans.stream().filter(t -> "COMPLETED".equals(t.getStatus())).count();
        long pendingCount = allTrans.stream().filter(t -> "PENDING".equals(t.getStatus())).count();
        long failedCount = allTrans.stream().filter(t -> "FAILED".equals(t.getStatus())).count();
        
        statsPanel.add(createInfoCard("Completed", String.valueOf(completedCount), new Color(46, 204, 113)));
        statsPanel.add(createInfoCard("Pending", String.valueOf(pendingCount), new Color(241, 196, 15)));
        statsPanel.add(createInfoCard("Failed", String.valueOf(failedCount), new Color(231, 76, 60)));
        
        panel.add(statsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadAllTransactions(DefaultTableModel model) {
        model.setRowCount(0);
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        
        for (Transaction trans : transactions) {
            // Get user name from account (use fromAccountId or toAccountId)
            String userName = "N/A";
            Integer accountId = trans.getFromAccountId() != null ? trans.getFromAccountId() : trans.getToAccountId();
            
            if (accountId != null && accountId > 0) {
                Account account = accountDAO.getAccountById(accountId);
                if (account != null) {
                    User user = userDAO.getUserById(account.getUserId());
                    if (user != null) {
                        userName = user.getFullName();
                    }
                }
            }
            
            model.addRow(new Object[]{
                trans.getTransactionId(),
                dateFormat.format(trans.getTransactionDate()),
                userName,
                trans.getTransactionType(),
                trans.getFromAccountNumber() != null ? trans.getFromAccountNumber() : "N/A",
                trans.getToAccountNumber() != null ? trans.getToAccountNumber() : "N/A",
                currencyFormat.format(trans.getAmount()),
                trans.getStatus(),
                trans.getDescription() != null ? trans.getDescription() : ""
            });
        }
    }
    
    private JPanel createInfoCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    // SERVICE APPROVALS PANEL
    private JPanel createServiceApprovalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Service Application Approvals");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Applications Table
        String[] columns = {"ID", "User ID", "Customer Name", "Username", "Service", "Amount", "Duration", "Date", "Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Only Actions column
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(70);   // User ID
        table.getColumnModel().getColumn(2).setPreferredWidth(150);  // Customer Name
        table.getColumnModel().getColumn(3).setPreferredWidth(120);  // Username
        table.getColumnModel().getColumn(4).setPreferredWidth(150);  // Service
        table.getColumnModel().getColumn(5).setPreferredWidth(100);  // Amount
        table.getColumnModel().getColumn(6).setPreferredWidth(80);   // Duration
        table.getColumnModel().getColumn(7).setPreferredWidth(150);  // Date
        table.getColumnModel().getColumn(8).setPreferredWidth(100);  // Status
        table.getColumnModel().getColumn(9).setPreferredWidth(180);  // Actions
        
        // Add action buttons renderer
        table.getColumn("Actions").setCellRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
            
            String status = (String) table.getValueAt(row, 8);
            
            if ("PENDING".equals(status)) {
                JButton approveBtn = new JButton("Approve");
                approveBtn.setFont(new Font("Arial", Font.PLAIN, 10));
                approveBtn.setBackground(new Color(46, 204, 113));
                approveBtn.setForeground(Color.WHITE);
                approveBtn.setFocusPainted(false);
                approveBtn.setBorderPainted(false);
                
                JButton rejectBtn = new JButton("Reject");
                rejectBtn.setFont(new Font("Arial", Font.PLAIN, 10));
                rejectBtn.setBackground(new Color(231, 76, 60));
                rejectBtn.setForeground(Color.WHITE);
                rejectBtn.setFocusPainted(false);
                rejectBtn.setBorderPainted(false);
                
                panel1.add(approveBtn);
                panel1.add(rejectBtn);
            } else {
                JLabel label = new JLabel(status);
                label.setFont(new Font("Arial", Font.BOLD, 11));
                label.setForeground("APPROVED".equals(status) ? new Color(46, 204, 113) : new Color(231, 76, 60));
                panel1.add(label);
            }
            
            return panel1;
        });
        
        table.getColumn("Actions").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
                
                String status = (String) table.getValueAt(row, 8);
                
                if ("PENDING".equals(status)) {
                    JButton approveBtn = new JButton("Approve");
                    approveBtn.setFont(new Font("Arial", Font.PLAIN, 10));
                    approveBtn.setBackground(new Color(46, 204, 113));
                    approveBtn.setForeground(Color.WHITE);
                    approveBtn.setFocusPainted(false);
                    approveBtn.setBorderPainted(false);
                    approveBtn.addActionListener(e -> approveApplication(table, row));
                    
                    JButton rejectBtn = new JButton("Reject");
                    rejectBtn.setFont(new Font("Arial", Font.PLAIN, 10));
                    rejectBtn.setBackground(new Color(231, 76, 60));
                    rejectBtn.setForeground(Color.WHITE);
                    rejectBtn.setFocusPainted(false);
                    rejectBtn.setBorderPainted(false);
                    rejectBtn.addActionListener(e -> rejectApplication(table, row));
                    
                    panel1.add(approveBtn);
                    panel1.add(rejectBtn);
                }
                
                return panel1;
            }
        });
        
        loadServiceApplications(model);
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Statistics Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        List<Object[]> allApps = serviceDAO.getPendingApplications();
        long pendingCount = allApps.stream().filter(a -> "PENDING".equals(a[8])).count();
        long approvedCount = allApps.stream().filter(a -> "APPROVED".equals(a[8])).count();
        long rejectedCount = allApps.stream().filter(a -> "REJECTED".equals(a[8])).count();
        
        statsPanel.add(createInfoCard("Pending", String.valueOf(pendingCount), new Color(241, 196, 15)));
        statsPanel.add(createInfoCard("Approved", String.valueOf(approvedCount), new Color(46, 204, 113)));
        statsPanel.add(createInfoCard("Rejected", String.valueOf(rejectedCount), new Color(231, 76, 60)));
        
        panel.add(statsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadServiceApplications(DefaultTableModel model) {
        model.setRowCount(0);
        List<Object[]> applications = serviceDAO.getPendingApplications();
        
        for (Object[] app : applications) {
            model.addRow(new Object[]{
                app[0], // ID
                String.format("%03d", (int)app[1]), // User ID (3-digit)
                app[2], // Customer Name
                app[3], // Username
                app[4], // Service Name
                currencyFormat.format((double)app[5]), // Amount
                app[6] + " months", // Duration
                dateFormat.format((Timestamp)app[7]), // Date
                app[8], // Status
                "" // Actions
            });
        }
    }
    
    private void approveApplication(JTable table, int row) {
        int appId = (int) table.getValueAt(row, 0);
        String customerName = (String) table.getValueAt(row, 2);
        String service = (String) table.getValueAt(row, 4);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Approve " + service + " application for " + customerName + "?",
            "Confirm Approval",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (serviceDAO.updateApplicationStatus(appId, "APPROVED")) {
                JOptionPane.showMessageDialog(this, 
                    "Application approved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshContent("APPROVALS");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to approve application.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void rejectApplication(JTable table, int row) {
        int appId = (int) table.getValueAt(row, 0);
        String customerName = (String) table.getValueAt(row, 2);
        String service = (String) table.getValueAt(row, 4);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Reject " + service + " application for " + customerName + "?",
            "Confirm Rejection",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (serviceDAO.updateApplicationStatus(appId, "REJECTED")) {
                JOptionPane.showMessageDialog(this,
                    "Application rejected.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshContent("APPROVALS");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to reject application.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // SYSTEM SETTINGS PANEL
    private JPanel createSystemSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("System Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Settings Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Bank Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Bank Name:"), gbc);
        
        JTextField bankNameField = new JTextField("MazeBank", 30);
        gbc.gridx = 1;
        formPanel.add(bankNameField, gbc);
        
        // Min Balance
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Minimum Balance:"), gbc);
        
        JTextField minBalanceField = new JTextField("500.00", 30);
        gbc.gridx = 1;
        formPanel.add(minBalanceField, gbc);
        
        // Max Daily Withdrawal
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Max Daily Withdrawal:"), gbc);
        
        JTextField maxWithdrawalField = new JTextField("10000.00", 30);
        gbc.gridx = 1;
        formPanel.add(maxWithdrawalField, gbc);
        
        // Transfer Fee
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Transfer Fee:"), gbc);
        
        JTextField transferFeeField = new JTextField("0.00", 30);
        gbc.gridx = 1;
        formPanel.add(transferFeeField, gbc);
        
        // Support Email
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Support Email:"), gbc);
        
        JTextField supportEmailField = new JTextField("support@mazebank.com", 30);
        gbc.gridx = 1;
        formPanel.add(supportEmailField, gbc);
        
        // Save Button
        JButton saveButton = new JButton("Save Settings");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(150, 35));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setOpaque(true);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Settings saved successfully!\n" +
                "Note: These settings are stored in the system_settings table.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(saveButton, gbc);
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void refreshContent(String view) {
        if ("USERS".equals(view)) {
            // Refresh user management table
            Component[] components = mainContentPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    Component[] subComps = ((JPanel) comp).getComponents();
                    for (Component subComp : subComps) {
                        if (subComp instanceof JScrollPane) {
                            JScrollPane scroll = (JScrollPane) subComp;
                            if (scroll.getViewport().getView() instanceof JTable) {
                                JTable table = (JTable) scroll.getViewport().getView();
                                DefaultTableModel model = (DefaultTableModel) table.getModel();
                                if (model.getColumnCount() == 8) { // User table
                                    loadUsers(model);
                                }
                            }
                        }
                    }
                }
            }
        } else if ("TRANSACTIONS".equals(view)) {
            // Refresh transaction table
            Component[] components = mainContentPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    Component[] subComps = ((JPanel) comp).getComponents();
                    for (Component subComp : subComps) {
                        if (subComp instanceof JScrollPane) {
                            JScrollPane scroll = (JScrollPane) subComp;
                            if (scroll.getViewport().getView() instanceof JTable) {
                                JTable table = (JTable) scroll.getViewport().getView();
                                DefaultTableModel model = (DefaultTableModel) table.getModel();
                                if (model.getColumnCount() == 8 && model.getColumnName(0).equals("Trans ID")) {
                                    loadAllTransactions(model);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}
