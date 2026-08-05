package com.repsync.controller;

import com.repsync.factory.WorkoutStrategyFactory;
import com.repsync.model.Exercise;
import com.repsync.model.User;
import com.repsync.model.enums.FitnessGoal;
import com.repsync.repository.ExerciseRepository;
import com.repsync.repository.UserRepository;
import com.repsync.service.BodyProfileCalculator;
import com.repsync.strategy.WorkoutStrategy;
import com.repsync.util.ExerciseGuideCatalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Goal-Oriented Workout Strategy Planning.
 * Exposes endpoints:
 * - GET /api/v1/workouts/plan (Uses logged-in user's fitness goal and body profile)
 * - GET /api/v1/workouts/plan/{goal} (Preview workout plan for any FitnessGoal)
 */
@RestController
@RequestMapping("/api/v1/workouts")
public class WorkoutController {

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    @Autowired
    public WorkoutController(ExerciseRepository exerciseRepository, UserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/plan")
    public ResponseEntity<Map<String, Object>> getPersonalizedWorkoutPlan(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        FitnessGoal goal = user.getFitnessGoal() != null ? user.getFitnessGoal() : FitnessGoal.STRENGTH;
        return ResponseEntity.ok(buildWorkoutPlanResponse(goal, user));
    }

    @GetMapping("/plan/{goalStr}")
    public ResponseEntity<Map<String, Object>> getPlanByGoal(@PathVariable String goalStr, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        FitnessGoal goal;
        try {
            goal = FitnessGoal.valueOf(goalStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            goal = FitnessGoal.STRENGTH;
        }
        return ResponseEntity.ok(buildWorkoutPlanResponse(goal, user));
    }

    private Map<String, Object> buildWorkoutPlanResponse(FitnessGoal goal, User user) {
        List<Exercise> allExercises = exerciseRepository.findAll();
        WorkoutStrategy strategy = WorkoutStrategyFactory.createStrategy(goal);

        List<Exercise> plannedExercises = strategy.generateExercises(allExercises).stream()
                .map(ex -> {
                    BodyProfileCalculator.personalizeExercise(ex, user);
                    ExerciseGuideCatalog.GuideInfo info = ExerciseGuideCatalog.getGuide(ex.getName(), ex.getMuscleGroup());
                    ex.setTargetRegion(info.targetRegion);
                    ex.setMachineSetup(info.machineSetup);
                    ex.setFormGuide(info.formGuide);
                    ex.setIllustrationType(info.illustrationType);
                    return ex;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("goal", goal.name());
        response.put("strategyName", strategy.getStrategyName());
        response.put("description", strategy.getDescription());
        response.put("anatomicalOverview", ExerciseGuideCatalog.getAnatomicalOverview(goal.getDisplayName()));
        response.put("personalizationSummary", BodyProfileCalculator.getPersonalizationSummary(user));
        response.put("exercises", plannedExercises);

        return response;
    }

    private User getUserFromAuth(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return createDefaultUser();
        }
        return userRepository.findByUsername(auth.getName())
                .or(() -> userRepository.findByEmail(auth.getName()))
                .orElseGet(this::createDefaultUser);
    }

    private User createDefaultUser() {
        User defaultUser = new User();
        defaultUser.setWeightKg(75.0);
        defaultUser.setHeightCm(175.0);
        defaultUser.setGender("MALE");
        defaultUser.setFitnessGoal(FitnessGoal.STRENGTH);
        return defaultUser;
    }
}
