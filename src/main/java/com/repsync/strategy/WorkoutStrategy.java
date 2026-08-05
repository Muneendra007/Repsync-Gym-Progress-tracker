package com.repsync.strategy;

import com.repsync.model.Exercise;
import java.util.List;

/**
 * Strategy interface for workout plan generation.
 * 
 * Demonstrates: Interface + Polymorphism (Strategy Design Pattern)
 * Each fitness goal has a different algorithm for selecting exercises.
 */
public interface WorkoutStrategy {

    /**
     * Generate a list of exercises for a workout based on this strategy.
     * 
     * @param availableExercises all exercises from the database
     * @return selected exercises with appropriate sets/reps/weight
     */
    List<Exercise> generateExercises(List<Exercise> availableExercises);

    /**
     * Get the name of this strategy.
     */
    String getStrategyName();

    /**
     * Get a description of what this strategy focuses on.
     */
    String getDescription();
}
