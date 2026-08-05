package com.repsync.service;

import com.repsync.dao.BodyProgressDAO;
import com.repsync.dao.WorkoutSessionDAO;
import com.repsync.model.BodyProgress;
import com.repsync.model.WorkoutSession;
import com.repsync.util.BMICalculator;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for tracking body progress and workout consistency.
 */
public class ProgressService {

    private final BodyProgressDAO progressDAO = new BodyProgressDAO();
    private final WorkoutSessionDAO sessionDAO = new WorkoutSessionDAO();

    /**
     * Log a new body weight entry.
     * Automatically calculates BMI if height is provided.
     */
    public int logWeight(int userId, double weightKg, double heightCm, String notes) {
        double bmi = BMICalculator.calculateBMI(weightKg, heightCm);

        BodyProgress progress = new BodyProgress(userId, weightKg, bmi, LocalDate.now());
        progress.setNotes(notes);

        return progressDAO.insert(progress);
    }

    /**
     * Get all weight entries for a user (for charting).
     */
    public List<BodyProgress> getWeightHistory(int userId) {
        return progressDAO.findByUserId(userId);
    }

    /**
     * Get the user's latest weight entry.
     */
    public BodyProgress getLatestWeight(int userId) {
        return progressDAO.getLatestWeight(userId);
    }

    /**
     * Get weight entries within a date range.
     */
    public List<BodyProgress> getWeightByDateRange(int userId, LocalDate start, LocalDate end) {
        return progressDAO.findByUserIdAndDateRange(userId, start, end);
    }

    /**
     * Calculate the current workout streak (consecutive days with workouts).
     * Counts backwards from today.
     */
    public int calculateWorkoutStreak(int userId) {
        List<WorkoutSession> sessions = sessionDAO.findByUserId(userId);
        if (sessions.isEmpty()) {
            return 0;
        }

        int streak = 0;
        LocalDate checkDate = LocalDate.now();

        // Go through sessions (already sorted newest first)
        for (WorkoutSession session : sessions) {
            LocalDate sessionDate = session.getSessionDate();

            if (sessionDate.equals(checkDate)) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else if (sessionDate.equals(checkDate.minusDays(1))) {
                // Allow a 1-day gap (worked out yesterday)
                checkDate = sessionDate;
                streak++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;  // Streak is broken
            }
        }

        return streak;
    }

    /**
     * Get workouts per week for the last N weeks (for charting).
     * Returns an array where index 0 is the most recent week.
     */
    public int[] getWeeklyWorkoutCounts(int userId, int weeks) {
        int[] counts = new int[weeks];
        LocalDate today = LocalDate.now();

        for (int i = 0; i < weeks; i++) {
            LocalDate weekEnd = today.minusWeeks(i);
            LocalDate weekStart = weekEnd.minusDays(6);

            List<WorkoutSession> weekSessions = sessionDAO.findByDateRange(userId, weekStart, weekEnd);
            counts[i] = weekSessions.size();
        }

        return counts;
    }
}
