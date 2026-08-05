package com.repsync.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single workout session (one gym visit).
 * 
 * Demonstrates: Composition (session CONTAINS multiple WorkoutExercise objects)
 * Collections: ArrayList for storing workout exercises
 */
public class WorkoutSession {

    private int id;
    private int userId;
    private Integer planId;          // nullable - session may not be from a plan
    private LocalDate sessionDate;
    private int durationMinutes;
    private String notes;

    // Composition - a session CONTAINS multiple logged exercises
    private List<WorkoutExercise> workoutExercises;

    public WorkoutSession() {
        this.workoutExercises = new ArrayList<>();
        this.sessionDate = LocalDate.now();
    }

    public WorkoutSession(int userId, LocalDate sessionDate, int durationMinutes) {
        this.userId = userId;
        this.sessionDate = sessionDate;
        this.durationMinutes = durationMinutes;
        this.workoutExercises = new ArrayList<>();
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<WorkoutExercise> getWorkoutExercises() {
        return workoutExercises;
    }

    public void setWorkoutExercises(List<WorkoutExercise> workoutExercises) {
        this.workoutExercises = workoutExercises;
    }

    /**
     * Add a logged exercise to this session.
     */
    public void addWorkoutExercise(WorkoutExercise workoutExercise) {
        this.workoutExercises.add(workoutExercise);
    }

    /**
     * Get total number of exercises in this session.
     */
    public int getExerciseCount() {
        return workoutExercises.size();
    }

    /**
     * Get total volume (sets × reps × weight) for this session.
     */
    public double getTotalVolume() {
        double total = 0;
        for (WorkoutExercise we : workoutExercises) {
            total += we.getSets() * we.getReps() * we.getWeightKg();
        }
        return total;
    }

    @Override
    public String toString() {
        return sessionDate + " - " + durationMinutes + " min (" + workoutExercises.size() + " exercises)";
    }
}
