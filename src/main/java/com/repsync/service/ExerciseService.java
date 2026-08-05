package com.repsync.service;

import com.repsync.dao.ExerciseDAO;
import com.repsync.model.Exercise;
import com.repsync.model.enums.Difficulty;
import com.repsync.model.enums.ExerciseType;

import java.util.List;

/**
 * Service class for exercise management.
 * Used by admin to add/edit/delete exercises.
 */
public class ExerciseService {

    private final ExerciseDAO exerciseDAO = new ExerciseDAO();

    /**
     * Get all exercises.
     */
    public List<Exercise> getAllExercises() {
        return exerciseDAO.findAll();
    }

    /**
     * Get exercise by ID.
     */
    public Exercise getExerciseById(int id) {
        return exerciseDAO.findById(id);
    }

    /**
     * Add a new exercise (admin only).
     */
    public int addExercise(Exercise exercise) {
        return exerciseDAO.insert(exercise);
    }

    /**
     * Update an existing exercise (admin only).
     */
    public boolean updateExercise(Exercise exercise) {
        return exerciseDAO.update(exercise);
    }

    /**
     * Delete an exercise (admin only).
     */
    public boolean deleteExercise(int exerciseId) {
        return exerciseDAO.delete(exerciseId);
    }

    /**
     * Get exercises filtered by muscle group.
     */
    public List<Exercise> getByMuscleGroup(String muscleGroup) {
        return exerciseDAO.findByMuscleGroup(muscleGroup);
    }

    /**
     * Get exercises filtered by difficulty.
     */
    public List<Exercise> getByDifficulty(Difficulty difficulty) {
        return exerciseDAO.findByDifficulty(difficulty);
    }

    /**
     * Get exercises filtered by type.
     */
    public List<Exercise> getByType(ExerciseType type) {
        return exerciseDAO.findByType(type);
    }

    /**
     * Search exercises by name keyword.
     */
    public List<Exercise> searchExercises(String keyword) {
        return exerciseDAO.search(keyword);
    }
}
