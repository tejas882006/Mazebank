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
 * LoginServlet - Handles user authentication
 * POST /api/login?username=user&password=pass
 */
public class LoginServlet extends HttpServlet {
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

            // Validate input
            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Username and password are required");
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            // Authenticate user
            User user = userDAO.login(username, password);
            if (user != null && user.isActive()) {
                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("fullName", user.getFullName());
                session.setAttribute("role", user.getRole());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes

                // Return success response
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("message", "Login successful");
                successResponse.put("userId", user.getUserId());
                successResponse.put("username", user.getUsername());
                successResponse.put("fullName", user.getFullName());
                successResponse.put("role", user.getRole());
                successResponse.put("sessionId", session.getId());
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(objectMapper.writeValueAsString(successResponse));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid username, password, or account inactive");
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
        msg.put("message", "Use POST method to login");
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.getWriter().print(new ObjectMapper().writeValueAsString(msg));
    }
}
