package com.repsync.dao;

import com.repsync.database.DatabaseConnection;
import com.repsync.model.User;
import com.repsync.model.enums.ExperienceLevel;
import com.repsync.model.enums.FitnessGoal;
import com.repsync.model.enums.UserRole;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User table.
 * Handles all database operations for users.
 */
public class UserDAO implements DAO<User> {

    @Override
    public int insert(User user) {
        String sql = "INSERT INTO users (username, password, email, role, age, gender, height_cm, weight_kg, fitness_goal, experience_level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole().name());
            stmt.setInt(5, user.getAge());
            stmt.setString(6, user.getGender());
            stmt.setDouble(7, user.getHeightCm());
            stmt.setDouble(8, user.getWeightKg());
            stmt.setString(9, user.getFitnessGoal() != null ? user.getFitnessGoal().name() : null);
            stmt.setString(10, user.getExperienceLevel() != null ? user.getExperienceLevel().name() : null);

            stmt.executeUpdate();

            // Get the auto-generated ID
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET username=?, email=?, role=?, age=?, gender=?, height_cm=?, weight_kg=?, fitness_goal=?, experience_level=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole().name());
            stmt.setInt(4, user.getAge());
            stmt.setString(5, user.getGender());
            stmt.setDouble(6, user.getHeightCm());
            stmt.setDouble(7, user.getWeightKg());
            stmt.setString(8, user.getFitnessGoal() != null ? user.getFitnessGoal().name() : null);
            stmt.setString(9, user.getExperienceLevel() != null ? user.getExperienceLevel().name() : null);
            stmt.setInt(10, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
        return false;
    }

    /**
     * Update user's password (separate from profile update for security).
     */
    public boolean updatePassword(int userId, String hashedPassword) {
        String sql = "UPDATE users SET password=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
        return false;
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by id: " + e.getMessage());
        }
        return null;
    }

    /**
     * Find a user by their username (used for login).
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }
        return null;
    }

    /**
     * Find a user by their email.
     */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY username";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Convert a database ResultSet row into a User object.
     * Helper method to avoid code duplication.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));

        // Parse role enum safely
        String roleStr = rs.getString("role");
        if (roleStr != null) {
            user.setRole(UserRole.valueOf(roleStr));
        }

        user.setAge(rs.getInt("age"));
        user.setGender(rs.getString("gender"));
        user.setHeightCm(rs.getDouble("height_cm"));
        user.setWeightKg(rs.getDouble("weight_kg"));

        // Parse fitness goal enum safely
        String goalStr = rs.getString("fitness_goal");
        if (goalStr != null) {
            user.setFitnessGoal(FitnessGoal.valueOf(goalStr));
        }

        // Parse experience level enum safely
        String expStr = rs.getString("experience_level");
        if (expStr != null) {
            user.setExperienceLevel(ExperienceLevel.valueOf(expStr));
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        return user;
    }
}
