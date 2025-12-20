package com.mazebank.servlet;

import com.mazebank.dao.AccountDAO;
import com.mazebank.dao.TransactionDAO;
import com.mazebank.model.Account;
import com.mazebank.model.Transaction;
import com.mazebank.exceptions.InsufficientFundsException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * WithdrawServlet - Handles withdrawal operations
 * POST /api/withdraw?accountId=id&amount=amount&description=desc
 */
public class WithdrawServlet extends HttpServlet {
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        accountDAO = new AccountDAO();
        transactionDAO = new TransactionDAO();
        objectMapper = new ObjectMapper();
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
            String accountIdStr = request.getParameter("accountId");
            String amountStr = request.getParameter("amount");
            String description = request.getParameter("description");

            // Validate input
            if (accountIdStr == null || amountStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "accountId and amount are required");
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            try {
                int accountId = Integer.parseInt(accountIdStr);
                BigDecimal amount = new BigDecimal(amountStr);

                // Validate amount
                if (amount.signum() <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Amount must be positive");
                    out.print(objectMapper.writeValueAsString(errorResponse));
                    return;
                }

                // Verify account belongs to user
                Account account = accountDAO.getAccountById(accountId);
                if (account == null || account.getUserId() != userId) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Account not found or access denied");
                    out.print(objectMapper.writeValueAsString(errorResponse));
                    return;
                }

                // Check sufficient funds
                if (account.getBalance().compareTo(amount) < 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Insufficient funds. Current balance: " + account.getBalance());
                    out.print(objectMapper.writeValueAsString(errorResponse));
                    return;
                }

                // Perform withdrawal
                BigDecimal newBalance = account.getBalance().subtract(amount);

                if (accountDAO.updateBalance(accountId, newBalance)) {
                    // Record transaction
                    Transaction transaction = new Transaction(accountId, null, "WITHDRAWAL", amount.negate(), description != null ? description : "Withdrawal");
                    transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()));
                    transactionDAO.createTransaction(transaction);

                    response.setStatus(HttpServletResponse.SC_OK);
                    Map<String, Object> successResponse = new HashMap<>();
                    successResponse.put("success", true);
                    successResponse.put("message", "Withdrawal successful");
                    successResponse.put("accountId", accountId);
                    successResponse.put("amount", amount);
                    successResponse.put("newBalance", newBalance);
                    out.print(objectMapper.writeValueAsString(successResponse));
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Failed to process withdrawal");
                    out.print(objectMapper.writeValueAsString(errorResponse));
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid account ID or amount format");
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        Map<String, String> msg = new HashMap<>();
        msg.put("message", "Use POST method to withdraw");
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.getWriter().print(new ObjectMapper().writeValueAsString(msg));
    }
}
