package com.mazebank.ui;

import com.mazebank.dao.*;
import com.mazebank.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer Dashboard
 * Main interface for customer banking operations
 */
public class CustomerDashboard extends JFrame {
    
    private User currentUser;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private BankingServiceDAO serviceDAO;
    private UserDAO userDAO;
    
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    
    private JLabel totalBalanceLabel;
    private JComboBox<String> accountSelector;
    private List<Account> userAccounts;
    
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    
    public CustomerDashboard(User user) {
        this.currentUser = user;
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        this.serviceDAO = new BankingServiceDAO();
        this.userDAO = new UserDAO();
        
        this.currencyFormat = NumberFormat.getCurrencyInstance();
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        
        // Initialize userAccounts as empty list to avoid NullPointerException
        this.userAccounts = new ArrayList<>();
        
        // Initialize UI first (creates all components)
        initializeUI();
        
        // Then load accounts (populates the components with data)
        loadUserAccounts();
        
        // Refresh overview to populate recent transactions
        refreshContent("OVERVIEW");
    }
    
    private void initializeUI() {
        setTitle("MazeBank - Customer Dashboard");
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
        mainContentPanel.add(createAccountOverviewPanel(), "OVERVIEW");
        mainContentPanel.add(createTransactionPanel(), "TRANSACTIONS");
        mainContentPanel.add(createServicesPanel(), "SERVICES");
        mainContentPanel.add(createProfilePanel(), "PROFILE");
        
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Show overview by default
        cardLayout.show(mainContentPanel, "OVERVIEW");
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setPreferredSize(new Dimension(1200, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Left side - Bank name
        JLabel bankLabel = new JLabel("MazeBank");
        bankLabel.setFont(new Font("Arial", Font.BOLD, 28));
        bankLabel.setForeground(Color.WHITE);
        
        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getFullName());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());
        
        rightPanel.add(userLabel);
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
            "Account Overview",
            "Transactions",
            "Banking Services",
            "Profile Management"
        };
        
        String[] navCommands = {
            "OVERVIEW",
            "TRANSACTIONS",
            "SERVICES",
            "PROFILE"
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
    
    // ACCOUNT OVERVIEW PANEL
    private JPanel createAccountOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Account Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Center content
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        
        // Balance Card
        JPanel balanceCard = createBalanceCard();
        centerPanel.add(balanceCard);
        
        // Recent Transactions
        JPanel recentTransPanel = createRecentTransactionsPanel();
        centerPanel.add(recentTransPanel);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBalanceCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Total Balance");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        titleLabel.setForeground(Color.WHITE);
        
        totalBalanceLabel = new JLabel("$0.00");
        totalBalanceLabel.setFont(new Font("Arial", Font.BOLD, 36));
        totalBalanceLabel.setForeground(Color.WHITE);
        
        // Account selector
        JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        accountPanel.setOpaque(false);
        JLabel accLabel = new JLabel("Select Account: ");
        accLabel.setForeground(Color.WHITE);
        accountSelector = new JComboBox<>();
        accountSelector.setPreferredSize(new Dimension(250, 30));
        accountPanel.add(accLabel);
        accountPanel.add(accountSelector);
        
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        contentPanel.setOpaque(false);
        contentPanel.add(titleLabel);
        contentPanel.add(totalBalanceLabel);
        contentPanel.add(accountPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Quick Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionsPanel.setOpaque(false);
        
        JButton depositBtn = createQuickActionButton("Deposit");
        JButton withdrawBtn = createQuickActionButton("Withdraw");
        JButton transferBtn = createQuickActionButton("Transfer");
        
        depositBtn.addActionListener(e -> showDepositDialog());
        withdrawBtn.addActionListener(e -> showWithdrawDialog());
        transferBtn.addActionListener(e -> showTransferDialog());
        
        actionsPanel.add(depositBtn);
        actionsPanel.add(withdrawBtn);
        actionsPanel.add(transferBtn);
        
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createQuickActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(110, 35));
        button.setBackground(new Color(46, 204, 113));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private JPanel createRecentTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Recent Transactions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Date", "Type", "Amount", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load recent transactions
        loadRecentTransactions(model);
        
        return panel;
    }
    
    private void loadRecentTransactions(DefaultTableModel model) {
        model.setRowCount(0);
        
        if (userAccounts.isEmpty()) return;
        
        // Get transactions for all user accounts and limit to 5 most recent
        List<Transaction> allTransactions = new ArrayList<>();
        for (Account account : userAccounts) {
            List<Transaction> transactions = transactionDAO.getRecentTransactions(account.getAccountId(), 5);
            allTransactions.addAll(transactions);
        }
        
        // Sort by transaction ID descending (most recent first) and limit to 5
        allTransactions.sort((t1, t2) -> Integer.compare(t2.getTransactionId(), t1.getTransactionId()));
        
        int count = 0;
        for (Transaction trans : allTransactions) {
            if (count >= 5) break;
            
            String description = "";
            if ("DEPOSIT".equals(trans.getTransactionType())) {
                description = "Deposit to " + (trans.getToAccountNumber() != null ? trans.getToAccountNumber() : "account");
            } else if ("WITHDRAWAL".equals(trans.getTransactionType())) {
                description = "Withdrawal from " + (trans.getFromAccountNumber() != null ? trans.getFromAccountNumber() : "account");
            } else if ("TRANSFER".equals(trans.getTransactionType())) {
                description = "Transfer: " + (trans.getFromAccountNumber() != null ? trans.getFromAccountNumber() : "N/A") + 
                            " → " + (trans.getToAccountNumber() != null ? trans.getToAccountNumber() : "N/A");
            } else {
                description = trans.getDescription() != null ? trans.getDescription() : "";
            }
            
            model.addRow(new Object[]{
                dateFormat.format(trans.getTransactionDate()),
                trans.getTransactionType(),
                currencyFormat.format(trans.getAmount()),
                description
            });
            count++;
        }
    }
    
    // TRANSACTION PANEL
    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Transaction ID", "Date", "Type", "From Account", "To Account", "Amount", "Status"};
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
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load transactions
        loadTransactions(model);
        
        return panel;
    }
    
    // SERVICES PANEL
    private JPanel createServicesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Banking Services");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Services Grid
        JPanel servicesGrid = new JPanel(new GridLayout(0, 2, 15, 15));
        servicesGrid.setBackground(Color.WHITE);
        
        List<BankingService> services = serviceDAO.getAllServices();
        for (BankingService service : services) {
            JPanel serviceCard = createServiceCard(service);
            servicesGrid.add(serviceCard);
        }
        
        JScrollPane scrollPane = new JScrollPane(servicesGrid);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createServiceCard(BankingService service) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(236, 240, 241));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel nameLabel = new JLabel(service.getServiceName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JTextArea descArea = new JTextArea(service.getDescription());
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        
        JLabel rateLabel = new JLabel("Interest Rate: " + service.getInterestRate() + "%");
        rateLabel.setFont(new Font("Arial", Font.BOLD, 13));
        rateLabel.setForeground(new Color(39, 174, 96));
        
        JButton applyButton = new JButton("Apply");
        applyButton.setBackground(new Color(52, 152, 219));
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.setBorderPainted(false);
        applyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        applyButton.addActionListener(e -> applyForService(service));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(nameLabel, BorderLayout.NORTH);
        topPanel.add(descArea, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(rateLabel, BorderLayout.WEST);
        bottomPanel.add(applyButton, BorderLayout.EAST);
        
        card.add(topPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    // PROFILE PANEL
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Profile Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(nameLabel, gbc);
        
        JTextField nameField = new JTextField(currentUser.getFullName(), 25);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(emailLabel, gbc);
        
        JTextField emailField = new JTextField(currentUser.getEmail(), 25);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(phoneLabel, gbc);
        
        JTextField phoneField = new JTextField(currentUser.getPhone() != null ? currentUser.getPhone() : "", 25);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        
        // Update button
        JButton updateButton = new JButton("Update Profile");
        updateButton.setFont(new Font("Arial", Font.BOLD, 14));
        updateButton.setPreferredSize(new Dimension(150, 35));
        updateButton.setBackground(new Color(46, 204, 113));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.setBorderPainted(false);
        updateButton.setOpaque(true);
        updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateButton.addActionListener(e -> updateProfile(nameField.getText(), emailField.getText(), phoneField.getText()));
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(updateButton, gbc);
        
        // Change Password Section
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Change Password"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordPanel.add(newPassLabel, gbc);
        
        JPasswordField newPasswordField = new JPasswordField(25);
        newPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        newPasswordField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        passwordPanel.add(newPasswordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        confirmPassLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordPanel.add(confirmPassLabel, gbc);
        
        JPasswordField confirmPasswordField = new JPasswordField(25);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        passwordPanel.add(confirmPasswordField, gbc);
        
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setFont(new Font("Arial", Font.BOLD, 14));
        changePasswordButton.setPreferredSize(new Dimension(180, 35));
        changePasswordButton.setBackground(new Color(231, 76, 60));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setFocusPainted(false);
        changePasswordButton.setBorderPainted(false);
        changePasswordButton.setOpaque(true);
        changePasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changePasswordButton.addActionListener(e -> changePassword(newPasswordField, confirmPasswordField));
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        passwordPanel.add(changePasswordButton, gbc);
        
        // Main container
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(formPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(passwordPanel);
        
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // HELPER METHODS
    private void loadUserAccounts() {
        userAccounts = accountDAO.getAccountsByUserId(currentUser.getUserId());
        accountSelector.removeAllItems();
        
        BigDecimal totalBalance = BigDecimal.ZERO;
        
        for (Account account : userAccounts) {
            String item = account.getAccountNumber() + " (" + account.getAccountType() + ") - " + 
                         currencyFormat.format(account.getBalance());
            accountSelector.addItem(item);
            totalBalance = totalBalance.add(account.getBalance());
        }
        
        totalBalanceLabel.setText(currencyFormat.format(totalBalance));
        
        if (userAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No accounts found. Please contact support.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void loadTransactions(DefaultTableModel model) {
        model.setRowCount(0);
        
        if (userAccounts.isEmpty()) return;
        
        for (Account account : userAccounts) {
            List<Transaction> transactions = transactionDAO.getTransactionsByAccountId(account.getAccountId());
            
            for (Transaction trans : transactions) {
                model.addRow(new Object[]{
                    trans.getTransactionId(),
                    dateFormat.format(trans.getTransactionDate()),
                    trans.getTransactionType(),
                    trans.getFromAccountNumber() != null ? trans.getFromAccountNumber() : "N/A",
                    trans.getToAccountNumber() != null ? trans.getToAccountNumber() : "N/A",
                    currencyFormat.format(trans.getAmount()),
                    trans.getStatus()
                });
            }
        }
    }
    
    private void showDepositDialog() {
        if (userAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No account available.");
            return;
        }
        
        String amountStr = JOptionPane.showInputDialog(this, "Enter deposit amount:");
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive.");
                    return;
                }
                
                Account selectedAccount = userAccounts.get(accountSelector.getSelectedIndex());
                BigDecimal newBalance = selectedAccount.getBalance().add(BigDecimal.valueOf(amount));
                
                if (accountDAO.updateBalance(selectedAccount.getAccountId(), newBalance)) {
                    Transaction transaction = new Transaction(
                        null,
                        selectedAccount.getAccountId(),
                        "DEPOSIT",
                        BigDecimal.valueOf(amount),
                        "Cash deposit"
                    );
                    transactionDAO.createTransaction(transaction);
                    
                    JOptionPane.showMessageDialog(this, "Deposit successful!");
                    loadUserAccounts();
                    refreshContent("OVERVIEW");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            }
        }
    }
    
    private void showWithdrawDialog() {
        if (userAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No account available.");
            return;
        }
        
        String amountStr = JOptionPane.showInputDialog(this, "Enter withdrawal amount:");
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive.");
                    return;
                }
                
                Account selectedAccount = userAccounts.get(accountSelector.getSelectedIndex());
                if (selectedAccount.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.");
                    return;
                }
                
                BigDecimal newBalance = selectedAccount.getBalance().subtract(BigDecimal.valueOf(amount));
                
                if (accountDAO.updateBalance(selectedAccount.getAccountId(), newBalance)) {
                    Transaction transaction = new Transaction(
                        selectedAccount.getAccountId(),
                        null,
                        "WITHDRAWAL",
                        BigDecimal.valueOf(amount),
                        "Cash withdrawal"
                    );
                    transactionDAO.createTransaction(transaction);
                    
                    JOptionPane.showMessageDialog(this, "Withdrawal successful!");
                    loadUserAccounts();
                    refreshContent("OVERVIEW");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            }
        }
    }
    
    private void showTransferDialog() {
        if (userAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No account available.");
            return;
        }
        
        String toAccountNumber = JOptionPane.showInputDialog(this, "Enter recipient account number:");
        if (toAccountNumber == null || toAccountNumber.trim().isEmpty()) return;
        
        Account toAccount = accountDAO.getAccountByNumber(toAccountNumber);
        if (toAccount == null) {
            JOptionPane.showMessageDialog(this, "Account not found.");
            return;
        }
        
        String amountStr = JOptionPane.showInputDialog(this, "Enter transfer amount:");
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive.");
                    return;
                }
                
                Account fromAccount = userAccounts.get(accountSelector.getSelectedIndex());
                if (fromAccount.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.");
                    return;
                }
                
                // Deduct from sender
                BigDecimal newFromBalance = fromAccount.getBalance().subtract(BigDecimal.valueOf(amount));
                // Add to receiver
                BigDecimal newToBalance = toAccount.getBalance().add(BigDecimal.valueOf(amount));
                
                if (accountDAO.updateBalance(fromAccount.getAccountId(), newFromBalance) &&
                    accountDAO.updateBalance(toAccount.getAccountId(), newToBalance)) {
                    
                    Transaction transaction = new Transaction(
                        fromAccount.getAccountId(),
                        toAccount.getAccountId(),
                        "TRANSFER",
                        BigDecimal.valueOf(amount),
                        "Transfer to " + toAccountNumber
                    );
                    transactionDAO.createTransaction(transaction);
                    
                    JOptionPane.showMessageDialog(this, "Transfer successful!");
                    loadUserAccounts();
                    refreshContent("OVERVIEW");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            }
        }
    }
    
    private void applyForService(BankingService service) {
        String amountStr = JOptionPane.showInputDialog(this, 
            "Enter amount for " + service.getServiceName() + ":");
        
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive.");
                    return;
                }
                
                int duration = 12; // Default duration
                if ("LOAN".equals(service.getServiceType())) {
                    String durationStr = JOptionPane.showInputDialog(this, "Enter duration (months):");
                    if (durationStr != null && !durationStr.trim().isEmpty()) {
                        duration = Integer.parseInt(durationStr);
                    }
                }
                
                if (serviceDAO.applyForService(currentUser.getUserId(), service.getServiceId(), amount, duration)) {
                    JOptionPane.showMessageDialog(this, 
                        "Application submitted successfully!\nStatus: PENDING\nYou will be notified once approved.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }
    
    private void updateProfile(String fullName, String email, String phone) {
        if (fullName.trim().isEmpty() || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and email cannot be empty.");
            return;
        }
        
        if (userDAO.updateProfile(currentUser.getUserId(), fullName, email, phone)) {
            currentUser.setFullName(fullName);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile.");
        }
    }
    
    private void changePassword(JPasswordField newPasswordField, JPasswordField confirmPasswordField) {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty.");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }
        
        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.");
            return;
        }
        
        if (userDAO.changePassword(currentUser.getUserId(), newPassword)) {
            JOptionPane.showMessageDialog(this, "Password changed successfully!");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to change password.");
        }
    }
    
    private void refreshContent(String view) {
        if ("OVERVIEW".equals(view)) {
            loadUserAccounts();
            // Refresh recent transactions in overview
            Component[] components = mainContentPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    findAndRefreshRecentTransactions((JPanel) comp);
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
                                if (model.getColumnCount() == 7) { // Full transaction table
                                    loadTransactions(model);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void findAndRefreshRecentTransactions(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JPanel) {
                        Component[] innerComps = ((JPanel) subComp).getComponents();
                        for (Component innerComp : innerComps) {
                            if (innerComp instanceof JScrollPane) {
                                JScrollPane scroll = (JScrollPane) innerComp;
                                if (scroll.getViewport().getView() instanceof JTable) {
                                    JTable table = (JTable) scroll.getViewport().getView();
                                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                                    if (model.getColumnCount() == 4) { // Recent transactions table
                                        loadRecentTransactions(model);
                                    }
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
