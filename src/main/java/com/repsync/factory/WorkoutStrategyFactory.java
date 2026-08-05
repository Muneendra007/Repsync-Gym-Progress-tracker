package com.repsync.factory;

import com.repsync.model.enums.FitnessGoal;
import com.repsync.strategy.*;

/**
 * Factory class for creating WorkoutStrategy objects.
 * 
 * Demonstrates: Factory Pattern
 * Returns the correct strategy based on the user's fitness goal.
 */
public class WorkoutStrategyFactory {

    /**
     * Create a workout strategy based on the fitness goal.
     * 
     * @param goal the user's fitness goal
     * @return the appropriate WorkoutStrategy implementation
     */
    public static WorkoutStrategy createStrategy(FitnessGoal goal) {
        return switch (goal) {
            case STRENGTH -> new StrengthStrategy();
            case MUSCLE_GAIN -> new MuscleGainStrategy();
            case FAT_LOSS -> new FatLossStrategy();
            case ENDURANCE -> new EnduranceStrategy();
        };
    }
}
