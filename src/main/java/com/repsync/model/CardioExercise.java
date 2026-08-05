package com.repsync.model;

import jakarta.persistence.*;
import com.repsync.model.enums.Difficulty;
import com.repsync.model.enums.ExerciseType;

/**
 * Cardio exercise - extends Exercise with cardio-specific defaults.
 * 
 * Demonstrates: Inheritance (CardioExercise IS-A Exercise)
 * Has default duration and calories burned estimate.
 */
@Entity
@DiscriminatorValue("CARDIO")
public class CardioExercise extends Exercise {

    @Column(name = "default_duration_seconds")
    private int defaultDurationSeconds;   // default duration in seconds

    @Transient
    private int defaultCaloriesBurned;    // estimated calories per session

    public CardioExercise() {
        super();
        setExerciseType(ExerciseType.CARDIO);
        this.defaultDurationSeconds = 1800;  // 30 minutes default
        this.defaultCaloriesBurned = 200;
    }

    public CardioExercise(String name, int defaultDurationSeconds) {
        super(name, ExerciseType.CARDIO, "CARDIO");
        this.defaultDurationSeconds = defaultDurationSeconds;
    }

    // Full constructor (used when reading from database)
    public CardioExercise(int id, String name, String muscleGroup, String equipment,
                          Difficulty difficulty, String description,
                          int defaultDurationSeconds) {
        super(id, name, ExerciseType.CARDIO, muscleGroup, equipment, difficulty, description);
        this.defaultDurationSeconds = defaultDurationSeconds;
    }

    // --- Getters and Setters ---

    public int getDefaultDurationSeconds() {
        return defaultDurationSeconds;
    }

    public void setDefaultDurationSeconds(int defaultDurationSeconds) {
        this.defaultDurationSeconds = defaultDurationSeconds;
    }

    public int getDefaultCaloriesBurned() {
        return defaultCaloriesBurned;
    }

    public void setDefaultCaloriesBurned(int defaultCaloriesBurned) {
        this.defaultCaloriesBurned = defaultCaloriesBurned;
    }

    /**
     * Get default duration formatted as "MM:SS".
     */
    public String getFormattedDuration() {
        int minutes = defaultDurationSeconds / 60;
        int seconds = defaultDurationSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return getName() + " (" + getFormattedDuration() + ")";
    }
}
