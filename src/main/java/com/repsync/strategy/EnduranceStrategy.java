package com.repsync.strategy;

import com.repsync.model.Exercise;
import com.repsync.model.StrengthExercise;
import com.repsync.model.enums.ExerciseType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Endurance workout strategy.
 * High reps (15-20+), light weight, emphasis on cardio.
 * 
 * Plan: 3 sets of 20 reps + extended cardio sessions
 * Builds muscular and cardiovascular endurance.
 */
public class EnduranceStrategy implements WorkoutStrategy {

    @Override
    public List<Exercise> generateExercises(List<Exercise> availableExercises) {
        List<Exercise> selected = new ArrayList<>();

        // Light strength work with high reps
        String[] targetGroups = {"LEGS", "BACK", "CORE", "SHOULDERS"};

        List<Exercise> strengthExercises = availableExercises.stream()
                .filter(e -> e.getExerciseType() == ExerciseType.STRENGTH)
                .collect(Collectors.toList());

        for (String group : targetGroups) {
            List<Exercise> groupExercises = strengthExercises.stream()
                    .filter(e -> group.equals(e.getMuscleGroup()))
                    .collect(Collectors.toList());

            if (!groupExercises.isEmpty()) {
                Exercise exercise = groupExercises.get(0);

                // Configure for endurance: 3 sets, 20 reps, light weight
                if (exercise instanceof StrengthExercise se) {
                    se.setDefaultSets(3);
                    se.setDefaultReps(20);
                    se.setDefaultWeight(se.getDefaultWeight() * 0.5); // 50% weight
                }

                selected.add(exercise);
            }
        }

        // Add #1 most intense stamina cardio exercise
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
        return "Endurance Training";
    }

    @Override
    public String getDescription() {
        return "High-rep training (3x20) with light weights plus extended cardio. Builds both muscular and cardiovascular endurance.";
    }
}
