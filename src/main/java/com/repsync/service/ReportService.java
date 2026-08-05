package com.repsync.service;

import com.repsync.model.WorkoutExercise;
import com.repsync.model.WorkoutSession;
import com.repsync.util.CSVExporter;
import com.repsync.util.DateFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for generating reports.
 * Implements the ReportGenerator interface concept.
 */
public class ReportService {

    private final WorkoutService workoutService = new WorkoutService();

    /**
     * Export workout history to a CSV file.
     * 
     * @param userId the user's ID
     * @param filePath the output file path
     * @return true if export was successful
     */
    public boolean exportWorkoutHistoryToCSV(int userId, String filePath) {
        List<WorkoutSession> sessions = workoutService.getWorkoutHistory(userId);

        String[] headers = {"Date", "Duration (min)", "Exercise", "Sets", "Reps", "Weight (kg)", "Notes"};
        List<String[]> data = new ArrayList<>();

        for (WorkoutSession session : sessions) {
            for (WorkoutExercise we : session.getWorkoutExercises()) {
                data.add(new String[]{
                    DateFormatter.formatShortDate(session.getSessionDate()),
                    String.valueOf(session.getDurationMinutes()),
                    we.getExerciseName(),
                    String.valueOf(we.getSets()),
                    String.valueOf(we.getReps()),
                    String.valueOf(we.getWeightKg()),
                    session.getNotes() != null ? session.getNotes() : ""
                });
            }
        }

        return CSVExporter.exportToCSV(filePath, headers, data);
    }

    /**
     * Get a monthly workout summary as formatted strings.
     */
    public List<String> getMonthlyReport(int userId, int year, int month) {
        List<String> report = new ArrayList<>();
        int count = workoutService.getWorkoutsInMonth(userId, year, month);
        report.add("Total Workouts: " + count);
        report.add("Month: " + month + "/" + year);
        return report;
    }
}
