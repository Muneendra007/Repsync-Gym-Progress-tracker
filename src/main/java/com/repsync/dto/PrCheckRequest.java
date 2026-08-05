package com.repsync.dto;

/**
 * Request payload for checking if a completed exercise set is a new Personal Record.
 */
public class PrCheckRequest {
    private int exerciseId;
    private double weightKg;
    private int reps;
    private int durationSeconds;
    private String exerciseName;

    public PrCheckRequest() {}

    public int getExerciseId() { return exerciseId; }
    public void setExerciseId(int exerciseId) { this.exerciseId = exerciseId; }
    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
}
