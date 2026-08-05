package com.repsync.model.enums;

/**
 * Fitness goals a user can select.
 * Each goal maps to a different workout strategy.
 */
public enum FitnessGoal {
    STRENGTH("Build Strength"),
    MUSCLE_GAIN("Build Muscle"),
    FAT_LOSS("Lose Fat"),
    ENDURANCE("Build Endurance");

    private final String displayName;

    FitnessGoal(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
