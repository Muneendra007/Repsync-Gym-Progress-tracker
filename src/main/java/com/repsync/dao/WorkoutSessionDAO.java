package com.repsync.dao;

import com.repsync.database.DatabaseConnection;
import com.repsync.model.WorkoutSession;
import com.repsync.model.WorkoutExercise;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for WorkoutSessions table.
 */
public class WorkoutSessionDAO implements DAO<WorkoutSession> {

    private final WorkoutExerciseDAO workoutExerciseDAO = new WorkoutExerciseDAO();

    @Override
    public int insert(WorkoutSession session) {
        String sql = "INSERT INTO workout_sessions (user_id, plan_id, session_date, duration_minutes, notes) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, session.getUserId());
            if (session.getPlanId() != null) {
                stmt.setInt(2, session.getPlanId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setDate(3, Date.valueOf(session.getSessionDate()));
            stmt.setInt(4, session.getDurationMinutes());
            stmt.setString(5, session.getNotes());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int sessionId = keys.getInt(1);

                // Also insert all workout exercises for this session
                for (WorkoutExercise we : session.getWorkoutExercises()) {
                    we.setSessionId(sessionId);
                    workoutExerciseDAO.insert(we);
                }

                return sessionId;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting workout session: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean update(WorkoutSession session) {
        String sql = "UPDATE workout_sessions SET session_date=?, duration_minutes=?, notes=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(session.getSessionDate()));
            stmt.setInt(2, session.getDurationMinutes());
            stmt.setString(3, session.getNotes());
            stmt.setInt(4, session.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating workout session: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM workout_sessions WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting workout session: " + e.getMessage());
        }
        return false;
    }

    @Override
    public WorkoutSession findById(int id) {
        String sql = "SELECT * FROM workout_sessions WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                WorkoutSession session = mapResultSetToSession(rs);
                // Load the exercises for this session
                session.setWorkoutExercises(workoutExerciseDAO.findBySessionId(session.getId()));
                return session;
            }
        } catch (SQLException e) {
            System.err.println("Error finding workout session: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<WorkoutSession> findAll() {
        String sql = "SELECT * FROM workout_sessions ORDER BY session_date DESC";
        List<WorkoutSession> sessions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                WorkoutSession session = mapResultSetToSession(rs);
                session.setWorkoutExercises(workoutExerciseDAO.findBySessionId(session.getId()));
                sessions.add(session);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all workout sessions: " + e.getMessage());
        }
        return sessions;
    }

    /**
     * Find all sessions for a specific user, ordered by date (newest first).
     */
    public List<WorkoutSession> findByUserId(int userId) {
        String sql = "SELECT * FROM workout_sessions WHERE user_id=? ORDER BY session_date DESC";
        List<WorkoutSession> sessions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                WorkoutSession session = mapResultSetToSession(rs);
                session.setWorkoutExercises(workoutExerciseDAO.findBySessionId(session.getId()));
                sessions.add(session);
            }
        } catch (SQLException e) {
            System.err.println("Error finding sessions by user: " + e.getMessage());
        }
        return sessions;
    }

    /**
     * Find sessions within a date range for a user.
     */
    public List<WorkoutSession> findByDateRange(int userId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM workout_sessions WHERE user_id=? AND session_date BETWEEN ? AND ? ORDER BY session_date DESC";
        List<WorkoutSession> sessions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                WorkoutSession session = mapResultSetToSession(rs);
                session.setWorkoutExercises(workoutExerciseDAO.findBySessionId(session.getId()));
                sessions.add(session);
            }
        } catch (SQLException e) {
            System.err.println("Error finding sessions by date range: " + e.getMessage());
        }
        return sessions;
    }

    /**
     * Count total sessions for a user in a specific month.
     */
    public int countSessionsInMonth(int userId, int year, int month) {
        String sql = "SELECT COUNT(*) FROM workout_sessions WHERE user_id=? AND YEAR(session_date)=? AND MONTH(session_date)=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, year);
            stmt.setInt(3, month);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting sessions: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get total number of sessions for a user.
     */
    public int getTotalSessions(int userId) {
        String sql = "SELECT COUNT(*) FROM workout_sessions WHERE user_id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting total sessions: " + e.getMessage());
        }
        return 0;
    }

    private WorkoutSession mapResultSetToSession(ResultSet rs) throws SQLException {
        WorkoutSession session = new WorkoutSession();
        session.setId(rs.getInt("id"));
        session.setUserId(rs.getInt("user_id"));

        int planId = rs.getInt("plan_id");
        if (!rs.wasNull()) {
            session.setPlanId(planId);
        }

        session.setSessionDate(rs.getDate("session_date").toLocalDate());
        session.setDurationMinutes(rs.getInt("duration_minutes"));
        session.setNotes(rs.getString("notes"));

        return session;
    }
}
