package com.repsync.strategy;

import com.repsync.model.Exercise;
import com.repsync.model.StrengthExercise;
import com.repsync.model.enums.ExerciseType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Muscle Gain (Hypertrophy) workout strategy.
 * Focuses on moderate weight, higher reps (8-12) and volume.
 * 
 * Plan: 4 sets of 8-12 reps — classic bodybuilding rep range
 * Includes more exercises per muscle group for maximum volume.
 */
public class MuscleGainStrategy implements WorkoutStrategy {

    @Override
    public List<Exercise> generateExercises(List<Exercise> availableExercises) {
        List<Exercise> selected = new ArrayList<>();

        // Filter to strength exercises
        List<Exercise> strengthExercises = availableExercises.stream()
                .filter(e -> e.getExerciseType() == ExerciseType.STRENGTH)
                .collect(Collectors.toList());

        // Pick 2 exercises per major muscle group for high volume
        String[] targetGroups = {"CHEST", "BACK", "LEGS", "SHOULDERS", "ARMS", "CORE"};

        for (String group : targetGroups) {
            List<Exercise> groupExercises = strengthExercises.stream()
                    .filter(e -> group.equals(e.getMuscleGroup()))
                    .collect(Collectors.toList());

            if (!groupExercises.isEmpty()) {
                // Pick the #1 most intense flagship exercise for this muscle group
                Exercise exercise = groupExercises.get(0);

                // Configure for hypertrophy: 4 sets, 10 reps
                if (exercise instanceof StrengthExercise se) {
                    se.setDefaultSets(4);
                    se.setDefaultReps(10);
                }

                selected.add(exercise);
            }
        }

        return selected;
    }

    @Override
    public String getStrategyName() {
        return "Muscle Gain (Hypertrophy)";
    }

    @Override
    public String getDescription() {
        return "High-volume training with 4 sets of 8-12 reps. Multiple exercises per muscle group for maximum muscle growth.";
    }
}
