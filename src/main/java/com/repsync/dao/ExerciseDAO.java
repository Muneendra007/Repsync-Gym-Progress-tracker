package com.repsync.dao;

import com.repsync.database.DatabaseConnection;
import com.repsync.model.Exercise;
import com.repsync.model.StrengthExercise;
import com.repsync.model.CardioExercise;
import com.repsync.model.enums.Difficulty;
import com.repsync.model.enums.ExerciseType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Exercise table.
 * Handles all database operations for exercises.
 * Returns StrengthExercise or CardioExercise based on exercise_type.
 */
public class ExerciseDAO implements DAO<Exercise> {

    @Override
    public int insert(Exercise exercise) {
        String sql = "INSERT INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg, default_duration_seconds) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, exercise.getName());
            stmt.setString(2, exercise.getExerciseType().name());
            stmt.setString(3, exercise.getMuscleGroup());
            stmt.setString(4, exercise.getEquipment());
            stmt.setString(5, exercise.getDifficulty() != null ? exercise.getDifficulty().name() : null);
            stmt.setString(6, exercise.getDescription());

            // Set type-specific defaults
            if (exercise instanceof StrengthExercise se) {
                stmt.setInt(7, se.getDefaultSets());
                stmt.setInt(8, se.getDefaultReps());
                stmt.setDouble(9, se.getDefaultWeight());
                stmt.setInt(10, 0);
            } else if (exercise instanceof CardioExercise ce) {
                stmt.setInt(7, 1);
                stmt.setInt(8, 1);
                stmt.setDouble(9, 0);
                stmt.setInt(10, ce.getDefaultDurationSeconds());
            } else {
                stmt.setInt(7, 3);
                stmt.setInt(8, 10);
                stmt.setDouble(9, 0);
                stmt.setInt(10, 0);
            }

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error inserting exercise: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean update(Exercise exercise) {
        String sql = "UPDATE exercises SET name=?, exercise_type=?, muscle_group=?, equipment=?, difficulty=?, description=?, default_sets=?, default_reps=?, default_weight_kg=?, default_duration_seconds=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, exercise.getName());
            stmt.setString(2, exercise.getExerciseType().name());
            stmt.setString(3, exercise.getMuscleGroup());
            stmt.setString(4, exercise.getEquipment());
            stmt.setString(5, exercise.getDifficulty() != null ? exercise.getDifficulty().name() : null);
            stmt.setString(6, exercise.getDescription());

            if (exercise instanceof StrengthExercise se) {
                stmt.setInt(7, se.getDefaultSets());
                stmt.setInt(8, se.getDefaultReps());
                stmt.setDouble(9, se.getDefaultWeight());
                stmt.setInt(10, 0);
            } else if (exercise instanceof CardioExercise ce) {
                stmt.setInt(7, 1);
                stmt.setInt(8, 1);
                stmt.setDouble(9, 0);
                stmt.setInt(10, ce.getDefaultDurationSeconds());
            } else {
                stmt.setInt(7, 3);
                stmt.setInt(8, 10);
                stmt.setDouble(9, 0);
                stmt.setInt(10, 0);
            }

            stmt.setInt(11, exercise.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating exercise: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM exercises WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting exercise: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Exercise findById(int id) {
        String sql = "SELECT * FROM exercises WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToExercise(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding exercise by id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Exercise> findAll() {
        String sql = "SELECT * FROM exercises ORDER BY name";
        List<Exercise> exercises = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                exercises.add(mapResultSetToExercise(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all exercises: " + e.getMessage());
        }
        return exercises;
    }

    /**
     * Find exercises by muscle group.
     */
    public List<Exercise> findByMuscleGroup(String muscleGroup) {
        String sql = "SELECT * FROM exercises WHERE muscle_group=? ORDER BY name";
        List<Exercise> exercises = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, muscleGroup);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                exercises.add(mapResultSetToExercise(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding exercises by muscle group: " + e.getMessage());
        }
        return exercises;
    }

    /**
     * Find exercises by difficulty level.
     */
    public List<Exercise> findByDifficulty(Difficulty difficulty) {
        String sql = "SELECT * FROM exercises WHERE difficulty=? ORDER BY name";
        List<Exercise> exercises = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, difficulty.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                exercises.add(mapResultSetToExercise(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding exercises by difficulty: " + e.getMessage());
        }
        return exercises;
    }

    /**
     * Find exercises by type (STRENGTH or CARDIO).
     */
    public List<Exercise> findByType(ExerciseType type) {
        String sql = "SELECT * FROM exercises WHERE exercise_type=? ORDER BY name";
        List<Exercise> exercises = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                exercises.add(mapResultSetToExercise(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding exercises by type: " + e.getMessage());
        }
        return exercises;
    }

    /**
     * Search exercises by name (partial match).
     */
    public List<Exercise> search(String keyword) {
        String sql = "SELECT * FROM exercises WHERE name LIKE ? ORDER BY name";
        List<Exercise> exercises = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                exercises.add(mapResultSetToExercise(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching exercises: " + e.getMessage());
        }
        return exercises;
    }

    /**
     * Convert a ResultSet row into the correct Exercise subtype.
     * Returns StrengthExercise or CardioExercise based on exercise_type.
     * 
     * Demonstrates: Polymorphism (returning different subtypes)
     */
    private Exercise mapResultSetToExercise(ResultSet rs) throws SQLException {
        String typeStr = rs.getString("exercise_type");
        ExerciseType type = ExerciseType.valueOf(typeStr);

        int id = rs.getInt("id");
        String name = rs.getString("name");
        String muscleGroup = rs.getString("muscle_group");
        String equipment = rs.getString("equipment");
        String diffStr = rs.getString("difficulty");
        Difficulty difficulty = diffStr != null ? Difficulty.valueOf(diffStr) : null;
        String description = rs.getString("description");

        if (type == ExerciseType.STRENGTH) {
            StrengthExercise exercise = new StrengthExercise(
                id, name, muscleGroup, equipment, difficulty, description,
                rs.getInt("default_sets"),
                rs.getInt("default_reps"),
                rs.getDouble("default_weight_kg")
            );
            return exercise;
        } else {
            CardioExercise exercise = new CardioExercise(
                id, name, muscleGroup, equipment, difficulty, description,
                rs.getInt("default_duration_seconds")
            );
            return exercise;
        }
    }
}
