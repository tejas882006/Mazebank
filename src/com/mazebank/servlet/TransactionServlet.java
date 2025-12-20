package com.mazebank.servlet;

import com.mazebank.dao.TransactionDAO;
import com.mazebank.dao.AccountDAO;
import com.mazebank.model.Transaction;
import com.mazebank.model.Account;
import com.mazebank.service.TransactionService;
import com.mazebank.exceptions.InsufficientFundsException;
import com.mazebank.exceptions.InvalidTransactionException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * TransactionServlet - Handles fund transfers and transaction history
 * POST /api/transfer - Transfer funds between accounts
 * GET /api/transactions - Get transaction history
 * GET /api/transactions/account/{accountId} - Get account-specific transactions
 */
public class TransactionServlet extends HttpServlet {
    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;
    private TransactionService transactionService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        transactionDAO = new TransactionDAO();
        accountDAO = new AccountDAO();
        transactionService = new TransactionService();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Check session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Session expired or invalid");
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            Integer userId = (Integer) session.getAttribute("userId");
            String pathInfo = request.getPathInfo();

            if (pathInfo != null && pathInfo.contains("/account/")) {
                // Get transactions for specific account: /api/transactions/account/{accountId}
                try {
                    int accountId = Integer.parseInt(pathInfo.substring(pathInfo.lastIndexOf("/") + 1));
                    
                    // Verify account belongs to user
                    if (accountDAO.getAccountById(accountId) == null ||
                        accountDAO.getAccountById(accountId).getUserId() != userId) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("success", false);
                        errorResponse.put("message", "Account not found or access denied");
                        out.print(objectMapper.writeValueAsString(errorResponse));
                        return;
                    }

                    List<Transaction> transactions = transactionDAO.getTransactionsByAccountId(accountId);
                    response.setStatus(HttpServletResponse.SC_OK);
                    Map<String, Object> successResponse = new HashMap<>();
                    successResponse.put("success", true);
                    successResponse.put("transactions", transactions);
                    successResponse.put("count", transactions.size());
                    out.print(objectMapper.writeValueAsString(successResponse));
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Invalid account ID");
                    out.print(objectMapper.writeValueAsString(errorResponse));
                }
            } else {
                // Get all user transactions: /api/transactions
                List<Transaction> transactions = transactionDAO.getAllTransactions();
                // Filter to user's accounts
                java.util.List<Account> userAccounts = accountDAO.getAccountsByUserId(userId);
                java.util.List<Integer> userAccountIds = new ArrayList<>();
                for (Account acc : userAccounts) {
                    userAccountIds.add(acc.getAccountId());
                }
                transactions.removeIf(t -> (t.getFromAccountId() == null || !userAccountIds.contains(t.getFromAccountId())) && 
                                            (t.getToAccountId() == null || !userAccountIds.contains(t.getToAccountId())));
                
                response.setStatus(HttpServletResponse.SC_OK);
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("transactions", transactions);
                successResponse.put("count", transactions.size());
                out.print(objectMapper.writeValueAsString(successResponse));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Server error: " + e.getMessage());
            out.print(objectMapper.writeValueAsString(errorResponse));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Check session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Session expired or invalid");
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            Integer userId = (Integer) session.getAttribute("userId");
            String fromAccountStr = request.getParameter("fromAccountId");
            String toAccountStr = request.getParameter("toAccountId");
            String amountStr = request.getParameter("amount");
            String description = request.getParameter("description");

            // Validate input
            if (fromAccountStr == null || toAccountStr == null || amountStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "fromAccountId, toAccountId, and amount are required");
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            try {
                int fromAccountId = Integer.parseInt(fromAccountStr);
                int toAccountId = Integer.parseInt(toAccountStr);
                BigDecimal amount = new BigDecimal(amountStr);

                // Verify source account belongs to user
                if (accountDAO.getAccountById(fromAccountId) == null ||
                    accountDAO.getAccountById(fromAccountId).getUserId() != userId) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Source account not found or access denied");
                    out.print(objectMapper.writeValueAsString(errorResponse));
                    return;
                }

                // Verify destination account exists
                if (accountDAO.getAccountById(toAccountId) == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Destination account not found");
                    out.print(objectMapper.writeValueAsString(errorResponse));
                    return;
                }

                // Perform transfer
                transactionService.transfer(fromAccountId, toAccountId, amount, description);

                response.setStatus(HttpServletResponse.SC_OK);
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("message", "Transfer completed successfully");
                successResponse.put("fromAccountId", fromAccountId);
                successResponse.put("toAccountId", toAccountId);
                successResponse.put("amount", amount);
                out.print(objectMapper.writeValueAsString(successResponse));

            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid account ID or amount format");
                out.print(objectMapper.writeValueAsString(errorResponse));
            } catch (InsufficientFundsException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Insufficient funds: " + e.getMessage());
                out.print(objectMapper.writeValueAsString(errorResponse));
            } catch (InvalidTransactionException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid transaction: " + e.getMessage());
                out.print(objectMapper.writeValueAsString(errorResponse));
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Database error: " + e.getMessage());
            out.print(objectMapper.writeValueAsString(errorResponse));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Server error: " + e.getMessage());
            out.print(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
