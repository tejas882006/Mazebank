package com.mazebank.servlet;

import com.mazebank.dao.UserDAO;
import com.mazebank.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

/**
 * SessionServlet - Handles session validation and user profile retrieval
 * GET /api/session - Get current session info
 */
public class SessionServlet extends HttpServlet {
    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
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
            User user = userDAO.getUserById(userId);

            if (user != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("userId", user.getUserId());
                successResponse.put("username", user.getUsername());
                successResponse.put("fullName", user.getFullName());
                successResponse.put("email", user.getEmail());
                successResponse.put("phone", user.getPhone());
                successResponse.put("role", user.getRole());
                successResponse.put("active", user.isActive());
                successResponse.put("sessionId", session.getId());
                out.print(objectMapper.writeValueAsString(successResponse));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "User not found");
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
