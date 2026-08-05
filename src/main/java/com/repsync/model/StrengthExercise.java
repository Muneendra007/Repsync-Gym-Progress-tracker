package com.repsync.model;

import jakarta.persistence.*;
import com.repsync.model.enums.Difficulty;
import com.repsync.model.enums.ExerciseType;

/**
 * Strength exercise - extends Exercise with strength-specific defaults.
 * 
 * Demonstrates: Inheritance (StrengthExercise IS-A Exercise)
 * Has default sets, reps, and weight for workout planning.
 */
@Entity
@DiscriminatorValue("STRENGTH")
public class StrengthExercise extends Exercise {

    @Column(name = "default_sets")
    private int defaultSets;

    @Column(name = "default_reps")
    private int defaultReps;

    @Column(name = "default_weight_kg")
    private double defaultWeight;   // in kg

    public StrengthExercise() {
        super();
        setExerciseType(ExerciseType.STRENGTH);
        this.defaultSets = 3;
        this.defaultReps = 10;
        this.defaultWeight = 0;
    }

    public StrengthExercise(String name, String muscleGroup, int defaultSets,
                            int defaultReps, double defaultWeight) {
        super(name, ExerciseType.STRENGTH, muscleGroup);
        this.defaultSets = defaultSets;
        this.defaultReps = defaultReps;
        this.defaultWeight = defaultWeight;
    }

    // Full constructor (used when reading from database)
    public StrengthExercise(int id, String name, String muscleGroup, String equipment,
                            Difficulty difficulty, String description,
                            int defaultSets, int defaultReps, double defaultWeight) {
        super(id, name, ExerciseType.STRENGTH, muscleGroup, equipment, difficulty, description);
        this.defaultSets = defaultSets;
        this.defaultReps = defaultReps;
        this.defaultWeight = defaultWeight;
    }

    // --- Getters and Setters ---

    public int getDefaultSets() {
        return defaultSets;
    }

    public void setDefaultSets(int defaultSets) {
        this.defaultSets = defaultSets;
    }

    public int getDefaultReps() {
        return defaultReps;
    }

    public void setDefaultReps(int defaultReps) {
        this.defaultReps = defaultReps;
    }

    public double getDefaultWeight() {
        return defaultWeight;
    }

    public void setDefaultWeight(double defaultWeight) {
        this.defaultWeight = defaultWeight;
    }

    @Override
    public String toString() {
        return getName() + " (" + defaultSets + "x" + defaultReps + " @ " + defaultWeight + "kg)";
    }
}
