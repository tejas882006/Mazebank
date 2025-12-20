package com.mazebank.servlet;

import com.mazebank.dao.AccountDAO;
import com.mazebank.model.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AccountServlet - Handles account operations
 * GET /api/account - Get user accounts
 * GET /api/account/{accountId} - Get specific account details
 * POST /api/account - Create new account
 */
public class AccountServlet extends HttpServlet {
    private AccountDAO accountDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        accountDAO = new AccountDAO();
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

            if (pathInfo != null && pathInfo.length() > 1) {
                // Get specific account: /api/account/{accountId}
                try {
                    int accountId = Integer.parseInt(pathInfo.substring(1));
                    Account account = accountDAO.getAccountById(accountId);
                    
                    if (account != null && account.getUserId() == userId) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        Map<String, Object> successResponse = new HashMap<>();
                        successResponse.put("success", true);
                        successResponse.put("account", account);
                        out.print(objectMapper.writeValueAsString(successResponse));
                    } else {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("success", false);
                        errorResponse.put("message", "Account not found or access denied");
                        out.print(objectMapper.writeValueAsString(errorResponse));
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Invalid account ID");
                    out.print(objectMapper.writeValueAsString(errorResponse));
                }
            } else {
                // Get all user accounts: /api/account
                List<Account> accounts = accountDAO.getAccountsByUserId(userId);
                response.setStatus(HttpServletResponse.SC_OK);
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("accounts", accounts);
                successResponse.put("count", accounts.size());
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
            String accountType = request.getParameter("accountType");
            String initialBalance = request.getParameter("initialBalance");

            // Validate input
            if (accountType == null || accountType.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Account type is required (SAVINGS or CURRENT)");
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            if (!accountType.equals("SAVINGS") && !accountType.equals("CURRENT")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid account type. Must be SAVINGS or CURRENT");
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            // Create new account
            Account newAccount = new Account();
            newAccount.setUserId(userId);
            newAccount.setAccountType(accountType);

            if (initialBalance != null && !initialBalance.trim().isEmpty()) {
                try {
                    newAccount.setBalance(new java.math.BigDecimal(initialBalance));
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Invalid balance format");
                    out.print(objectMapper.writeValueAsString(errorResponse));
                    return;
                }
            }

            if (accountDAO.createAccount(newAccount)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("message", "Account created successfully");
                successResponse.put("account", newAccount);
                out.print(objectMapper.writeValueAsString(successResponse));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Failed to create account");
                out.print(objectMapper.writeValueAsString(errorResponse));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Server error: " + e.getMessage());
            out.print(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
