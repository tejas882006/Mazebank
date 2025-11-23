package com.mazebank.dao;

import com.mazebank.model.User;

import java.util.List;

/**
 * DAO interface for User operations
 */
public interface IUserDAO {
    User login(String username, String password);
    boolean registerUser(User user);
    List<User> getAllUsers();
    User getUserById(int userId);
    boolean updateUser(User user);
    boolean updateProfile(int userId, String fullName, String email, String phone);
    boolean changePassword(int userId, String newPassword);
    boolean deleteUser(int userId);
    boolean deleteUserPermanently(int userId);
    boolean usernameExists(String username);
    boolean emailExists(String email);
}
