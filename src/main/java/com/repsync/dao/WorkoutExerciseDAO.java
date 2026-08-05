package com.repsync.dao;

import com.repsync.database.DatabaseConnection;
import com.repsync.model.WorkoutExercise;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for WorkoutExercises table.
 * Tracks individual exercises performed within a workout session.
 */
public class WorkoutExerciseDAO implements DAO<WorkoutExercise> {

    @Override
    public int insert(WorkoutExercise workoutExercise) {
        String sql = "INSERT INTO workout_exercises (session_id, exercise_id, sets, reps, weight_kg, duration_seconds) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, workoutExercise.getSessionId());
            stmt.setInt(2, workoutExercise.getExerciseId());
            stmt.setInt(3, workoutExercise.getSets());
            stmt.setInt(4, workoutExercise.getReps());
            stmt.setDouble(5, workoutExercise.getWeightKg());
            stmt.setInt(6, workoutExercise.getDurationSeconds());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error inserting workout exercise: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean update(WorkoutExercise workoutExercise) {
        String sql = "UPDATE workout_exercises SET sets=?, reps=?, weight_kg=?, duration_seconds=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, workoutExercise.getSets());
            stmt.setInt(2, workoutExercise.getReps());
            stmt.setDouble(3, workoutExercise.getWeightKg());
            stmt.setInt(4, workoutExercise.getDurationSeconds());
            stmt.setInt(5, workoutExercise.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating workout exercise: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM workout_exercises WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting workout exercise: " + e.getMessage());
        }
        return false;
    }

    @Override
    public WorkoutExercise findById(int id) {
        String sql = "SELECT we.*, e.name AS exercise_name FROM workout_exercises we JOIN exercises e ON we.exercise_id = e.id WHERE we.id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding workout exercise: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<WorkoutExercise> findAll() {
        String sql = "SELECT we.*, e.name AS exercise_name FROM workout_exercises we JOIN exercises e ON we.exercise_id = e.id";
        List<WorkoutExercise> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all workout exercises: " + e.getMessage());
        }
        return list;
    }

    /**
     * Find all exercises logged in a specific session.
     */
    public List<WorkoutExercise> findBySessionId(int sessionId) {
        String sql = "SELECT we.*, e.name AS exercise_name FROM workout_exercises we JOIN exercises e ON we.exercise_id = e.id WHERE we.session_id=?";
        List<WorkoutExercise> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding exercises for session: " + e.getMessage());
        }
        return list;
    }

    private WorkoutExercise mapResultSet(ResultSet rs) throws SQLException {
        WorkoutExercise we = new WorkoutExercise();
        we.setId(rs.getInt("id"));
        we.setSessionId(rs.getInt("session_id"));
        we.setExerciseId(rs.getInt("exercise_id"));
        we.setSets(rs.getInt("sets"));
        we.setReps(rs.getInt("reps"));
        we.setWeightKg(rs.getDouble("weight_kg"));
        we.setDurationSeconds(rs.getInt("duration_seconds"));
        we.setExerciseName(rs.getString("exercise_name"));
        return we;
    }
}
