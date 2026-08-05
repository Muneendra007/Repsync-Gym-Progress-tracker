package com.repsync.model.enums;

/**
 * Types of personal records that can be tracked.
 */
public enum RecordType {
    MAX_WEIGHT("Max Weight (kg)"),
    MAX_REPS("Max Reps"),
    MAX_DURATION("Max Duration (seconds)");

    private final String displayName;

    RecordType(String displayName) {
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
