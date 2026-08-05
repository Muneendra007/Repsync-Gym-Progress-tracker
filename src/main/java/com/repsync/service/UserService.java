package com.repsync.service;

import com.repsync.dao.UserDAO;
import com.repsync.model.User;
import com.repsync.model.enums.FitnessGoal;

/**
 * Service class for user profile operations.
 */
public class UserService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Update user profile information.
     */
    public boolean updateProfile(User user) {
        return userDAO.update(user);
    }

    /**
     * Get user by ID.
     */
    public User getUserById(int id) {
        return userDAO.findById(id);
    }

    /**
     * Update user's fitness goal.
     */
    public boolean updateFitnessGoal(int userId, FitnessGoal goal) {
        User user = userDAO.findById(userId);
        if (user != null) {
            user.setFitnessGoal(goal);
            return userDAO.update(user);
        }
        return false;
    }
}
