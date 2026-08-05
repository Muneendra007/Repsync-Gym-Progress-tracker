package com.repsync.dao;

import com.repsync.database.DatabaseConnection;
import com.repsync.model.enums.FitnessGoal;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for WorkoutPlans table.
 * Stores generated workout plans for users.
 * 
 * Note: This DAO works with raw plan data (not the abstract WorkoutPlan class),
 * since plans are stored as simple records linking users to goals.
 */
public class WorkoutPlanDAO {

    /**
     * Insert a new workout plan and return its ID.
     */
    public int insert(int userId, String planName, FitnessGoal goal) {
        String sql = "INSERT INTO workout_plans (user_id, plan_name, fitness_goal) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setString(2, planName);
            stmt.setString(3, goal.name());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error inserting workout plan: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Delete a workout plan.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM workout_plans WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting workout plan: " + e.getMessage());
        }
        return false;
    }

    /**
     * Find all plans for a specific user.
     * Returns list of maps with plan details.
     */
    public List<Map<String, Object>> findByUserId(int userId) {
        String sql = "SELECT * FROM workout_plans WHERE user_id=? ORDER BY created_at DESC";
        List<Map<String, Object>> plans = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> plan = new HashMap<>();
                plan.put("id", rs.getInt("id"));
                plan.put("userId", rs.getInt("user_id"));
                plan.put("planName", rs.getString("plan_name"));
                plan.put("fitnessGoal", rs.getString("fitness_goal"));
                plan.put("createdAt", rs.getTimestamp("created_at"));
                plans.add(plan);
            }
        } catch (SQLException e) {
            System.err.println("Error finding plans for user: " + e.getMessage());
        }
        return plans;
    }

    /**
     * Find a plan by its ID.
     */
    public Map<String, Object> findById(int id) {
        String sql = "SELECT * FROM workout_plans WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> plan = new HashMap<>();
                plan.put("id", rs.getInt("id"));
                plan.put("userId", rs.getInt("user_id"));
                plan.put("planName", rs.getString("plan_name"));
                plan.put("fitnessGoal", rs.getString("fitness_goal"));
                plan.put("createdAt", rs.getTimestamp("created_at"));
                return plan;
            }
        } catch (SQLException e) {
            System.err.println("Error finding plan: " + e.getMessage());
        }
        return null;
    }
}
