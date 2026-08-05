package com.repsync.model;

/**
 * Represents a single exercise performed within a workout session.
 * Links an Exercise to a WorkoutSession with specific sets/reps/weight.
 * 
 * Demonstrates: Composition (WorkoutSession contains WorkoutExercise objects)
 * This is the "bridge" between a session and the exercises done in it.
 */
public class WorkoutExercise {

    private int id;
    private int sessionId;
    private int exerciseId;
    private int sets;
    private int reps;
    private double weightKg;
    private int durationSeconds;     // for cardio exercises

    // Reference to the exercise details (for display purposes)
    private String exerciseName;

    public WorkoutExercise() {
    }

    public WorkoutExercise(int exerciseId, int sets, int reps, double weightKg) {
        this.exerciseId = exerciseId;
        this.sets = sets;
        this.reps = reps;
        this.weightKg = weightKg;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    /**
     * Calculate total volume for this exercise (sets × reps × weight).
     */
    public double getVolume() {
        return sets * reps * weightKg;
    }

    @Override
    public String toString() {
        if (weightKg > 0) {
            return exerciseName + ": " + sets + "x" + reps + " @ " + weightKg + "kg";
        } else {
            return exerciseName + ": " + durationSeconds + "s";
        }
    }
}
