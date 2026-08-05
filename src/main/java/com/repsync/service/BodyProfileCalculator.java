package com.repsync.service;

import com.repsync.model.Exercise;
import com.repsync.model.CardioExercise;
import com.repsync.model.StrengthExercise;
import com.repsync.model.User;
import com.repsync.model.enums.ExperienceLevel;
import com.repsync.model.enums.FitnessGoal;

/**
 * Computes personalized exercise parameters (sets, reps, weight) based on
 * the user's body profile: weight, height, age, gender, fitness goal, and
 * experience level.
 *
 * Uses evidence-based strength standards to scale compound and isolation lifts
 * relative to body weight, then adjusts for goal and experience.
 */
public class BodyProfileCalculator {

    /**
     * Calculate the user's BMI.
     */
    public static double calculateBMI(double weightKg, double heightCm) {
        if (heightCm <= 0 || weightKg <= 0) return 0;
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    /**
     * Get a human-readable BMI classification.
     */
    public static String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal Weight";
        if (bmi < 30.0) return "Overweight";
        return "Obese";
    }

    /**
     * Get a recommended fitness goal based on BMI (for suggestion only).
     */
    public static FitnessGoal suggestGoal(double bmi) {
        if (bmi < 18.5) return FitnessGoal.MUSCLE_GAIN;
        if (bmi < 25.0) return FitnessGoal.STRENGTH;
        if (bmi < 30.0) return FitnessGoal.FAT_LOSS;
        return FitnessGoal.FAT_LOSS;
    }

    /**
     * Configure an exercise with personalized sets, reps, and weight based on
     * the user's body profile and fitness goal.
     */
    public static void personalizeExercise(Exercise exercise, User user) {
        if (user == null || !user.isProfileComplete()) {
            return; // No personalization without body data
        }

        FitnessGoal goal = user.getFitnessGoal();
        ExperienceLevel exp = user.getExperienceLevel() != null ? user.getExperienceLevel() : ExperienceLevel.BEGINNER;

        if (exercise instanceof StrengthExercise se) {
            personalizeStrength(se, user, goal, exp);
        } else if (exercise instanceof CardioExercise ce) {
            personalizeCardio(ce, user, goal, exp);
        }
    }

    // ─────────────────────────────────────────
    //  STRENGTH EXERCISE PERSONALIZATION
    // ─────────────────────────────────────────

    private static void personalizeStrength(StrengthExercise se, User user, FitnessGoal goal, ExperienceLevel exp) {
        double bodyWeight = user.getWeightKg();

        // 1. Compute sets and reps based on goal
        int sets, reps;
        switch (goal) {
            case STRENGTH -> { sets = 5; reps = 5; }
            case FAT_LOSS -> { sets = 3; reps = 15; }
            case MUSCLE_GAIN -> { sets = 4; reps = 10; }
            case ENDURANCE -> { sets = 3; reps = 20; }
            default -> { sets = 3; reps = 10; }
        }

        // 2. Adjust sets/reps for experience
        switch (exp) {
            case BEGINNER -> { reps = Math.max(reps, 8); } // Beginners do more reps with lighter weight
            case ADVANCED -> { sets += 1; }                 // Advanced lifters get extra volume
            default -> {}
        }

        se.setDefaultSets(sets);
        se.setDefaultReps(reps);

        // 3. Compute personalized weight
        double weight = computePersonalizedWeight(se, bodyWeight, user, goal, exp);
        se.setDefaultWeight(Math.round(weight * 4.0) / 4.0); // Round to nearest 0.25 kg
    }

    /**
     * Compute a personalized weight for a strength exercise based on:
     * - Body weight × exercise ratio (compound vs isolation)
     * - Gender adjustment
     * - Experience multiplier
     * - Goal multiplier (heavier for strength, lighter for endurance/fat loss)
     */
    private static double computePersonalizedWeight(StrengthExercise se, double bodyWeight,
                                                     User user, FitnessGoal goal, ExperienceLevel exp) {
        String name = se.getName().toLowerCase();

        // Exercise-specific body-weight ratios for a beginner male
        double bwRatio = getExerciseBWRatio(name);

        // Base weight from body weight
        double baseWeight = bodyWeight * bwRatio;

        // Gender adjustment (research-based average ratio)
        String gender = user.getGender();
        if ("FEMALE".equalsIgnoreCase(gender)) {
            baseWeight *= 0.65;
        }

        // Age adjustment (slight decrease for older users)
        int age = user.getAge();
        if (age > 50) {
            baseWeight *= 0.85;
        } else if (age > 40) {
            baseWeight *= 0.92;
        } else if (age < 18) {
            baseWeight *= 0.70;
        }

        // Experience multiplier
        double expMultiplier = switch (exp) {
            case BEGINNER -> 0.60;
            case INTERMEDIATE -> 0.85;
            case ADVANCED -> 1.10;
        };
        baseWeight *= expMultiplier;

        // Goal multiplier (% of working max)
        double goalMultiplier = switch (goal) {
            case STRENGTH -> 0.85;     // Heavy, close to max
            case MUSCLE_GAIN -> 0.70;  // Moderate-heavy
            case FAT_LOSS -> 0.55;     // Lighter for circuit/high-rep
            case ENDURANCE -> 0.50;    // Lightest for endurance reps
        };
        baseWeight *= goalMultiplier;

        // Floor: minimum weight is 0 (bodyweight exercises)
        return Math.max(baseWeight, 0);
    }

    /**
     * Get an approximate body-weight ratio for common exercises.
     * E.g., a beginner male can bench ~0.5× BW, squat ~0.75× BW, etc.
     * These are conservative starting points.
     */
    private static double getExerciseBWRatio(String exerciseName) {
        // Compound lifts — higher BW ratios
        if (exerciseName.contains("squat")) return 0.75;
        if (exerciseName.contains("deadlift")) return 0.85;
        if (exerciseName.contains("bench press")) return 0.50;
        if (exerciseName.contains("incline") && exerciseName.contains("press")) return 0.40;
        if (exerciseName.contains("decline") && exerciseName.contains("press")) return 0.45;
        if (exerciseName.contains("overhead press") || exerciseName.contains("shoulder press")) return 0.35;
        if (exerciseName.contains("barbell row") || exerciseName.contains("row")) return 0.45;
        if (exerciseName.contains("hip thrust")) return 0.65;
        if (exerciseName.contains("leg press")) return 1.10;
        if (exerciseName.contains("romanian deadlift")) return 0.60;

        // Isolation lifts — lower BW ratios
        if (exerciseName.contains("curl")) return 0.18;
        if (exerciseName.contains("skull crush") || exerciseName.contains("skull")) return 0.20;
        if (exerciseName.contains("pushdown") || exerciseName.contains("extension")) return 0.18;
        if (exerciseName.contains("lateral raise")) return 0.08;
        if (exerciseName.contains("fly") || exerciseName.contains("cable")) return 0.12;
        if (exerciseName.contains("face pull")) return 0.15;
        if (exerciseName.contains("leg extension") || exerciseName.contains("leg curl")) return 0.30;
        if (exerciseName.contains("calf")) return 0.40;
        if (exerciseName.contains("cable crunch") || exerciseName.contains("crunch")) return 0.25;

        // Bodyweight exercises — no external weight
        if (exerciseName.contains("push up") || exerciseName.contains("pull up")
            || exerciseName.contains("dip") || exerciseName.contains("plank")
            || exerciseName.contains("burpee") || exerciseName.contains("mountain")) {
            return 0.0;
        }

        // Default: moderate isolation ratio
        return 0.20;
    }

    // ─────────────────────────────────────────
    //  CARDIO EXERCISE PERSONALIZATION
    // ─────────────────────────────────────────

    private static void personalizeCardio(CardioExercise ce, User user, FitnessGoal goal, ExperienceLevel exp) {
        int baseDuration = ce.getDefaultDurationSeconds();
        if (baseDuration <= 0) baseDuration = 1800; // Default 30 min

        // Goal adjustment
        double goalMult = switch (goal) {
            case FAT_LOSS -> 1.2;     // More cardio for fat loss
            case ENDURANCE -> 1.4;    // Even more for endurance
            case STRENGTH -> 0.6;     // Less cardio for strength
            case MUSCLE_GAIN -> 0.7;  // Less for hypertrophy
        };

        // Experience adjustment
        double expMult = switch (exp) {
            case BEGINNER -> 0.7;
            case INTERMEDIATE -> 1.0;
            case ADVANCED -> 1.3;
        };

        int personalizedDuration = (int) (baseDuration * goalMult * expMult);
        // Clamp between 5 min and 60 min
        personalizedDuration = Math.max(300, Math.min(3600, personalizedDuration));
        ce.setDefaultDurationSeconds(personalizedDuration);
    }

    /**
     * Get a short description of the personalization applied to this user.
     */
    public static String getPersonalizationSummary(User user) {
        if (user == null || !user.isProfileComplete()) {
            return "Default weights (no body profile set).";
        }

        double bmi = calculateBMI(user.getWeightKg(), user.getHeightCm());
        String category = getBMICategory(bmi);
        ExperienceLevel exp = user.getExperienceLevel() != null ? user.getExperienceLevel() : ExperienceLevel.BEGINNER;

        return String.format("Personalized for %.0fkg / %.0fcm (%s, BMI %.1f) • %s • %s",
                user.getWeightKg(), user.getHeightCm(), user.getGender(),
                bmi, category, exp.getDisplayName());
    }
}
