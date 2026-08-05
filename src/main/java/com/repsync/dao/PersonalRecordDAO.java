package com.repsync.dao;

import com.repsync.database.DatabaseConnection;
import com.repsync.model.PersonalRecord;
import com.repsync.model.enums.RecordType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for PersonalRecords table.
 * Handles PR storage and retrieval.
 */
public class PersonalRecordDAO implements DAO<PersonalRecord> {

    @Override
    public int insert(PersonalRecord pr) {
        String sql = "INSERT INTO personal_records (user_id, exercise_id, record_value, record_type, achieved_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pr.getUserId());
            stmt.setInt(2, pr.getExerciseId());
            stmt.setDouble(3, pr.getRecordValue());
            stmt.setString(4, pr.getRecordType().name());
            stmt.setDate(5, Date.valueOf(pr.getAchievedDate()));

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error inserting personal record: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean update(PersonalRecord pr) {
        String sql = "UPDATE personal_records SET record_value=?, achieved_date=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, pr.getRecordValue());
            stmt.setDate(2, Date.valueOf(pr.getAchievedDate()));
            stmt.setInt(3, pr.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating personal record: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM personal_records WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting personal record: " + e.getMessage());
        }
        return false;
    }

    @Override
    public PersonalRecord findById(int id) {
        String sql = "SELECT pr.*, e.name AS exercise_name FROM personal_records pr JOIN exercises e ON pr.exercise_id = e.id WHERE pr.id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding personal record: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<PersonalRecord> findAll() {
        String sql = "SELECT pr.*, e.name AS exercise_name FROM personal_records pr JOIN exercises e ON pr.exercise_id = e.id ORDER BY pr.achieved_date DESC";
        List<PersonalRecord> records = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                records.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all personal records: " + e.getMessage());
        }
        return records;
    }

    /**
     * Find all PRs for a specific user.
     */
    public List<PersonalRecord> findAllByUser(int userId) {
        String sql = "SELECT pr.*, e.name AS exercise_name FROM personal_records pr JOIN exercises e ON pr.exercise_id = e.id WHERE pr.user_id=? ORDER BY pr.achieved_date DESC";
        List<PersonalRecord> records = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                records.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding PRs for user: " + e.getMessage());
        }
        return records;
    }

    /**
     * Get the latest PR for a specific user and exercise of a given type.
     * Returns null if no PR exists.
     */
    public PersonalRecord getLatestPR(int userId, int exerciseId, RecordType recordType) {
        String sql = "SELECT pr.*, e.name AS exercise_name FROM personal_records pr JOIN exercises e ON pr.exercise_id = e.id WHERE pr.user_id=? AND pr.exercise_id=? AND pr.record_type=? ORDER BY pr.record_value DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, exerciseId);
            stmt.setString(3, recordType.name());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting latest PR: " + e.getMessage());
        }
        return null;
    }

    /**
     * Find all PRs for a specific user and exercise.
     */
    public List<PersonalRecord> findByUserAndExercise(int userId, int exerciseId) {
        String sql = "SELECT pr.*, e.name AS exercise_name FROM personal_records pr JOIN exercises e ON pr.exercise_id = e.id WHERE pr.user_id=? AND pr.exercise_id=? ORDER BY pr.achieved_date DESC";
        List<PersonalRecord> records = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, exerciseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                records.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding PRs: " + e.getMessage());
        }
        return records;
    }

    private PersonalRecord mapResultSet(ResultSet rs) throws SQLException {
        PersonalRecord pr = new PersonalRecord();
        pr.setId(rs.getInt("id"));
        pr.setUserId(rs.getInt("user_id"));
        pr.setExerciseId(rs.getInt("exercise_id"));
        pr.setRecordValue(rs.getDouble("record_value"));
        pr.setRecordType(RecordType.valueOf(rs.getString("record_type")));
        pr.setAchievedDate(rs.getDate("achieved_date").toLocalDate());
        pr.setExerciseName(rs.getString("exercise_name"));
        return pr;
    }
}
