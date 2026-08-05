package com.repsync.strategy;

import com.repsync.model.Exercise;
import com.repsync.model.StrengthExercise;
import com.repsync.model.enums.ExerciseType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strength-focused workout strategy.
 * Focuses on heavy compound lifts with low reps and high weight.
 * 
 * Plan: 5x5 style — 5 sets of 5 reps with heavy weights
 * Targets: Big compound movements (Squat, Bench, Deadlift, OHP)
 */
public class StrengthStrategy implements WorkoutStrategy {

    @Override
    public List<Exercise> generateExercises(List<Exercise> availableExercises) {
        List<Exercise> selected = new ArrayList<>();

        // Filter to strength exercises only
        List<Exercise> strengthExercises = availableExercises.stream()
                .filter(e -> e.getExerciseType() == ExerciseType.STRENGTH)
                .collect(Collectors.toList());

        // Pick exercises from different muscle groups for a balanced workout
        String[] targetGroups = {"CHEST", "BACK", "LEGS", "SHOULDERS", "ARMS"};

        for (String group : targetGroups) {
            // Find exercises for this muscle group
            List<Exercise> groupExercises = strengthExercises.stream()
                    .filter(e -> group.equals(e.getMuscleGroup()))
                    .collect(Collectors.toList());

            if (!groupExercises.isEmpty()) {
                // Pick the first exercise from each group
                Exercise exercise = groupExercises.get(0);

                // Configure for strength: 5 sets, 5 reps (heavy)
                if (exercise instanceof StrengthExercise se) {
                    se.setDefaultSets(5);
                    se.setDefaultReps(5);
                    // Increase weight by 10% for strength focus
                    se.setDefaultWeight(se.getDefaultWeight() * 1.1);
                }

                selected.add(exercise);
            }
        }

        return selected;
    }

    @Override
    public String getStrategyName() {
        return "Strength Training (5x5)";
    }

    @Override
    public String getDescription() {
        return "Heavy compound lifts with 5 sets of 5 reps. Focus on progressive overload with Squat, Bench Press, Deadlift, and Overhead Press.";
    }
}
