package com.repsync.service;

import com.repsync.dao.WorkoutSessionDAO;
import com.repsync.dao.WorkoutExerciseDAO;
import com.repsync.model.WorkoutSession;
import com.repsync.model.WorkoutExercise;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for workout session management.
 * Handles logging workouts and retrieving history.
 */
public class WorkoutService {

    private final WorkoutSessionDAO sessionDAO = new WorkoutSessionDAO();
    private final WorkoutExerciseDAO exerciseDAO = new WorkoutExerciseDAO();

    /**
     * Log a complete workout session with all exercises.
     * 
     * @param session the workout session to save
     * @return the session ID, or -1 if failed
     */
    public int logWorkout(WorkoutSession session) {
        return sessionDAO.insert(session);
    }

    /**
     * Get all workout sessions for a user.
     */
    public List<WorkoutSession> getWorkoutHistory(int userId) {
        return sessionDAO.findByUserId(userId);
    }

    /**
     * Get sessions within a date range.
     */
    public List<WorkoutSession> getWorkoutsByDateRange(int userId, LocalDate start, LocalDate end) {
        return sessionDAO.findByDateRange(userId, start, end);
    }

    /**
     * Get a specific session by ID.
     */
    public WorkoutSession getSessionById(int sessionId) {
        return sessionDAO.findById(sessionId);
    }

    /**
     * Delete a workout session.
     */
    public boolean deleteSession(int sessionId) {
        return sessionDAO.delete(sessionId);
    }

    /**
     * Get total number of workouts for a user.
     */
    public int getTotalWorkouts(int userId) {
        return sessionDAO.getTotalSessions(userId);
    }

    /**
     * Get number of workouts in a specific month.
     */
    public int getWorkoutsInMonth(int userId, int year, int month) {
        return sessionDAO.countSessionsInMonth(userId, year, month);
    }
}
