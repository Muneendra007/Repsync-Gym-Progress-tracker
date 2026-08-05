package com.repsync.model;

import com.repsync.model.enums.FitnessGoal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract WorkoutPlan - base class for all workout plans.
 * 
 * Demonstrates: Abstraction (abstract class with abstract method)
 * Composition: Contains a list of Exercise objects
 */
public abstract class WorkoutPlan {

    private int id;
    private int userId;
    private String planName;
    private FitnessGoal fitnessGoal;
    private LocalDateTime createdAt;

    // Composition - a workout plan CONTAINS multiple exercises
    private List<Exercise> exercises;

    public WorkoutPlan() {
        this.exercises = new ArrayList<>();
    }

    public WorkoutPlan(int userId, String planName, FitnessGoal fitnessGoal) {
        this.userId = userId;
        this.planName = planName;
        this.fitnessGoal = fitnessGoal;
        this.exercises = new ArrayList<>();
    }

    /**
     * Abstract method - each plan type generates exercises differently.
     * Demonstrates: Abstraction
     * 
     * @param availableExercises all exercises from the database
     * @return the list of exercises selected for this plan
     */
    public abstract List<Exercise> generatePlan(List<Exercise> availableExercises);

    /**
     * Get a description of what this plan focuses on.
     */
    public abstract String getPlanDescription();

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public FitnessGoal getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(FitnessGoal fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    /**
     * Add a single exercise to this plan.
     */
    public void addExercise(Exercise exercise) {
        this.exercises.add(exercise);
    }

    /**
     * Remove an exercise from this plan.
     */
    public void removeExercise(Exercise exercise) {
        this.exercises.remove(exercise);
    }

    /**
     * Get the total number of exercises in this plan.
     */
    public int getExerciseCount() {
        return exercises.size();
    }

    @Override
    public String toString() {
        return planName + " (" + fitnessGoal + " - " + exercises.size() + " exercises)";
    }
}
