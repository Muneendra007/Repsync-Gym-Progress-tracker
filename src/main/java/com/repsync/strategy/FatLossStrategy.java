package com.repsync.strategy;

import com.repsync.model.Exercise;
import com.repsync.model.StrengthExercise;
import com.repsync.model.enums.ExerciseType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fat Loss workout strategy.
 * Combines strength training with cardio for maximum calorie burn.
 * 
 * Plan: Circuit-style — 3 sets of 15 reps with lower weight + cardio
 * High reps, shorter rest, includes cardio exercises.
 */
public class FatLossStrategy implements WorkoutStrategy {

    @Override
    public List<Exercise> generateExercises(List<Exercise> availableExercises) {
        List<Exercise> selected = new ArrayList<>();

        // Get one strength exercise per major group (circuit style)
        String[] targetGroups = {"CHEST", "BACK", "LEGS", "SHOULDERS", "CORE"};

        List<Exercise> strengthExercises = availableExercises.stream()
                .filter(e -> e.getExerciseType() == ExerciseType.STRENGTH)
                .collect(Collectors.toList());

        for (String group : targetGroups) {
            List<Exercise> groupExercises = strengthExercises.stream()
                    .filter(e -> group.equals(e.getMuscleGroup()))
                    .collect(Collectors.toList());

            if (!groupExercises.isEmpty()) {
                Exercise exercise = groupExercises.get(0);

                // Configure for fat loss: 3 sets, 15 reps, lighter weight
                if (exercise instanceof StrengthExercise se) {
                    se.setDefaultSets(3);
                    se.setDefaultReps(15);
                    se.setDefaultWeight(se.getDefaultWeight() * 0.7); // 70% weight
                }

                selected.add(exercise);
            }
        }

        // Add #1 most intense metabolic cardio exercise
        List<Exercise> cardioExercises = availableExercises.stream()
                .filter(e -> e.getExerciseType() == ExerciseType.CARDIO)
                .collect(Collectors.toList());

        if (!cardioExercises.isEmpty()) {
            selected.add(cardioExercises.get(0));
        }

        return selected;
    }

    @Override
    public String getStrategyName() {
        return "Fat Loss (Circuit)";
    }

    @Override
    public String getDescription() {
        return "Circuit-style training with 3 sets of 15 reps at lighter weight, combined with cardio. Short rest periods for maximum calorie burn.";
    }
}
