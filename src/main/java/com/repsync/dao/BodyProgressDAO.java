package com.repsync.dao;

import com.repsync.database.DatabaseConnection;
import com.repsync.model.BodyProgress;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for BodyProgress table.
 * Tracks body weight and BMI over time.
 */
public class BodyProgressDAO implements DAO<BodyProgress> {

    @Override
    public int insert(BodyProgress progress) {
        String sql = "INSERT INTO body_progress (user_id, weight_kg, bmi, record_date, notes) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, progress.getUserId());
            stmt.setDouble(2, progress.getWeightKg());
            stmt.setDouble(3, progress.getBmi());
            stmt.setDate(4, Date.valueOf(progress.getRecordDate()));
            stmt.setString(5, progress.getNotes());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error inserting body progress: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean update(BodyProgress progress) {
        String sql = "UPDATE body_progress SET weight_kg=?, bmi=?, record_date=?, notes=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, progress.getWeightKg());
            stmt.setDouble(2, progress.getBmi());
            stmt.setDate(3, Date.valueOf(progress.getRecordDate()));
            stmt.setString(4, progress.getNotes());
            stmt.setInt(5, progress.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating body progress: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM body_progress WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting body progress: " + e.getMessage());
        }
        return false;
    }

    @Override
    public BodyProgress findById(int id) {
        String sql = "SELECT * FROM body_progress WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding body progress: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<BodyProgress> findAll() {
        String sql = "SELECT * FROM body_progress ORDER BY record_date DESC";
        List<BodyProgress> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all body progress: " + e.getMessage());
        }
        return list;
    }

    /**
     * Find all progress entries for a user, ordered by date.
     */
    public List<BodyProgress> findByUserId(int userId) {
        String sql = "SELECT * FROM body_progress WHERE user_id=? ORDER BY record_date ASC";
        List<BodyProgress> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding body progress for user: " + e.getMessage());
        }
        return list;
    }

    /**
     * Find progress entries within a date range.
     */
    public List<BodyProgress> findByUserIdAndDateRange(int userId, LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM body_progress WHERE user_id=? AND record_date BETWEEN ? AND ? ORDER BY record_date ASC";
        List<BodyProgress> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding body progress by date range: " + e.getMessage());
        }
        return list;
    }

    /**
     * Get the user's latest weight entry.
     */
    public BodyProgress getLatestWeight(int userId) {
        String sql = "SELECT * FROM body_progress WHERE user_id=? ORDER BY record_date DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting latest weight: " + e.getMessage());
        }
        return null;
    }

    private BodyProgress mapResultSet(ResultSet rs) throws SQLException {
        BodyProgress bp = new BodyProgress();
        bp.setId(rs.getInt("id"));
        bp.setUserId(rs.getInt("user_id"));
        bp.setWeightKg(rs.getDouble("weight_kg"));
        bp.setBmi(rs.getDouble("bmi"));
        bp.setRecordDate(rs.getDate("record_date").toLocalDate());
        bp.setNotes(rs.getString("notes"));
        return bp;
    }
}
