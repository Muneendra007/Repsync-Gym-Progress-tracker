package com.repsync.controller;

import com.repsync.exception.ResourceNotFoundException;
import com.repsync.model.Exercise;
import com.repsync.model.enums.ExerciseType;
import com.repsync.repository.ExerciseRepository;
import com.repsync.util.ExerciseGuideCatalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Exercise Library and Anatomical Guidance.
 * Exposes endpoints:
 * - GET /api/v1/exercises
 * - GET /api/v1/exercises/{id}
 * - GET /api/v1/exercises/muscle/{group}
 * - GET /api/v1/exercises/type/{type}
 * Enriches every Exercise with targetRegion, machineSetup, formGuide, and illustrationType from ExerciseGuideCatalog.
 */
@RestController
@RequestMapping("/api/v1/exercises")
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ExerciseController(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises() {
        List<Exercise> exercises = exerciseRepository.findAll().stream()
                .map(this::enrichWithGuide)
                .collect(Collectors.toList());
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable int id) {
        Exercise exercise = exerciseRepository.findById(id)
                .map(this::enrichWithGuide)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with ID: " + id));
        return ResponseEntity.ok(exercise);
    }

    @GetMapping("/muscle/{group}")
    public ResponseEntity<List<Exercise>> getExercisesByMuscleGroup(@PathVariable String group) {
        List<Exercise> exercises = exerciseRepository.findByMuscleGroup(group).stream()
                .map(this::enrichWithGuide)
                .collect(Collectors.toList());
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Exercise>> getExercisesByType(@PathVariable ExerciseType type) {
        List<Exercise> exercises = exerciseRepository.findByExerciseType(type).stream()
                .map(this::enrichWithGuide)
                .collect(Collectors.toList());
        return ResponseEntity.ok(exercises);
    }

    private Exercise enrichWithGuide(Exercise exercise) {
        if (exercise != null) {
            ExerciseGuideCatalog.GuideInfo info = ExerciseGuideCatalog.getGuide(exercise.getName(), exercise.getMuscleGroup());
            exercise.setTargetRegion(info.targetRegion);
            exercise.setMachineSetup(info.machineSetup);
            exercise.setFormGuide(info.formGuide);
            exercise.setIllustrationType(info.illustrationType);
        }
        return exercise;
    }
}
