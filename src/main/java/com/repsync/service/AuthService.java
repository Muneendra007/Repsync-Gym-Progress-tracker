package com.repsync.service;

import com.repsync.dao.UserDAO;
import com.repsync.model.User;
import com.repsync.util.InputValidator;
import com.repsync.util.PasswordHasher;
import com.repsync.util.exceptions.AuthException;

/**
 * Service class for authentication operations.
 * Handles registration, login, and password changes.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Register a new user.
     * Validates input, checks for duplicates, hashes password.
     * 
     * @param username the desired username
     * @param password the desired password
     * @param email the user's email
     * @return the newly created User
     * @throws AuthException if validation fails or username/email already exists
     */
    public User register(String username, String password, String email) throws AuthException {
        // Validate inputs
        if (!InputValidator.isValidUsername(username)) {
            throw new AuthException("Username must be 3-50 characters (letters, numbers, underscores only).");
        }
        if (!InputValidator.isValidPassword(password)) {
            throw new AuthException("Password must be at least 6 characters long.");
        }
        if (!InputValidator.isValidEmail(email)) {
            throw new AuthException("Please enter a valid email address.");
        }

        // Check if username already exists
        if (userDAO.findByUsername(username) != null) {
            throw new AuthException("Username '" + username + "' is already taken.");
        }

        // Check if email already exists
        if (userDAO.findByEmail(email) != null) {
            throw new AuthException("Email '" + email + "' is already registered.");
        }

        // Hash the password and create the user
        String hashedPassword = PasswordHasher.hash(password);
        User newUser = new User(username, hashedPassword, email);

        int userId = userDAO.insert(newUser);
        if (userId == -1) {
            throw new AuthException("Failed to create account. Please try again.");
        }

        newUser.setId(userId);
        return newUser;
    }

    /**
     * Login with username and password.
     * 
     * @param username the username
     * @param password the plain-text password
     * @return the authenticated User
     * @throws AuthException if credentials are invalid
     */
    public User login(String username, String password) throws AuthException {
        if (!InputValidator.isNotEmpty(username) || !InputValidator.isNotEmpty(password)) {
            throw new AuthException("Please enter both username and password.");
        }

        // Find user by username
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new AuthException("Invalid username or password.");
        }

        // Verify password
        if (!PasswordHasher.verify(password, user.getPassword())) {
            throw new AuthException("Invalid username or password.");
        }

        return user;
    }

    /**
     * Change a user's password.
     * 
     * @param userId the user's ID
     * @param oldPassword the current password
     * @param newPassword the new password
     * @throws AuthException if old password is wrong or new password is invalid
     */
    public void changePassword(int userId, String oldPassword, String newPassword) throws AuthException {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new AuthException("User not found.");
        }

        // Verify old password
        if (!PasswordHasher.verify(oldPassword, user.getPassword())) {
            throw new AuthException("Current password is incorrect.");
        }

        // Validate new password
        if (!InputValidator.isValidPassword(newPassword)) {
            throw new AuthException("New password must be at least 6 characters long.");
        }

        // Hash and update
        String hashedNewPassword = PasswordHasher.hash(newPassword);
        boolean success = userDAO.updatePassword(userId, hashedNewPassword);

        if (!success) {
            throw new AuthException("Failed to change password. Please try again.");
        }
    }
}
