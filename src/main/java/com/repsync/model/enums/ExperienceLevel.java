package com.repsync.model.enums;

/**
 * Training experience level reported by the user.
 * Used to scale workout intensity and load personalization.
 * Distinct from exercise Difficulty — this describes the user, not the exercise.
 */
public enum ExperienceLevel {
    BEGINNER("Beginner (< 6 months)"),
    INTERMEDIATE("Intermediate (6 months – 2 years)"),
    ADVANCED("Advanced (2+ years)");

    private final String displayName;

    ExperienceLevel(String displayName) {
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
