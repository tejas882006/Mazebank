package com.mazebank.servlet;

import com.mazebank.dao.UserDAO;
import com.mazebank.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

/**
 * RegistrationServlet - Handles user registration
 * POST /api/register?username=user&password=pass&fullName=name&email=email&phone=phone
 */
public class RegistrationServlet extends HttpServlet {
    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");

            // Validate input
            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty() ||
                email == null || email.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Username, password, full name, and email are required");
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            // Check if username already exists
            if (userDAO.usernameExists(username)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Username already exists");
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            // Check if email already exists
            if (userDAO.emailExists(email)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Email already registered");
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            // Create new user
            User newUser = new User(username, password, fullName, email, "CUSTOMER");
            if (phone != null && !phone.trim().isEmpty()) {
                newUser.setPhone(phone);
            }

            // Register user
            if (userDAO.registerUser(newUser)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("message", "User registered successfully");
                successResponse.put("username", username);
                out.print(objectMapper.writeValueAsString(successResponse));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Failed to register user");
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
        msg.put("message", "Use POST method to register");
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.getWriter().print(new ObjectMapper().writeValueAsString(msg));
    }
}
