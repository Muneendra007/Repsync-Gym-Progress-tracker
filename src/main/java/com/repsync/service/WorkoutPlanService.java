package com.repsync.service;

import com.repsync.dao.ExerciseDAO;
import com.repsync.dao.WorkoutPlanDAO;
import com.repsync.factory.WorkoutStrategyFactory;
import com.repsync.model.Exercise;
import com.repsync.model.enums.FitnessGoal;
import com.repsync.strategy.WorkoutStrategy;

import java.util.List;

/**
 * Service class for workout plan generation.
 * Uses the Strategy Pattern to generate plans based on fitness goals.
 */
public class WorkoutPlanService {

    private final ExerciseDAO exerciseDAO = new ExerciseDAO();
    private final WorkoutPlanDAO planDAO = new WorkoutPlanDAO();

    /**
     * Generate a workout plan based on the user's fitness goal.
     * Uses the Strategy Pattern to select the right algorithm.
     * 
     * @param userId the user's ID
     * @param goal the fitness goal
     * @return list of exercises for the generated plan
     */
    public List<Exercise> generatePlan(int userId, FitnessGoal goal) {
        // Get all available exercises from the database
        List<Exercise> allExercises = exerciseDAO.findAll();

        // Use Factory to create the right strategy (Polymorphism)
        WorkoutStrategy strategy = WorkoutStrategyFactory.createStrategy(goal);

        // Generate exercises using the strategy
        List<Exercise> planExercises = strategy.generateExercises(allExercises);

        // Save the plan to the database
        String planName = strategy.getStrategyName();
        planDAO.insert(userId, planName, goal);

        return planExercises;
    }

    /**
     * Get the strategy description for a fitness goal.
     */
    public String getStrategyDescription(FitnessGoal goal) {
        WorkoutStrategy strategy = WorkoutStrategyFactory.createStrategy(goal);
        return strategy.getDescription();
    }

    /**
     * Get the strategy name for a fitness goal.
     */
    public String getStrategyName(FitnessGoal goal) {
        WorkoutStrategy strategy = WorkoutStrategyFactory.createStrategy(goal);
        return strategy.getStrategyName();
    }
}
