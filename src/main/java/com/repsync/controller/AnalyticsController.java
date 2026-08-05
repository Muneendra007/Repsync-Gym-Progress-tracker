package com.repsync.controller;

import com.repsync.dto.PrCheckRequest;
import com.repsync.model.PersonalRecord;
import com.repsync.model.User;
import com.repsync.model.enums.RecordType;
import com.repsync.repository.PersonalRecordRepository;
import com.repsync.repository.UserRepository;
import com.repsync.service.BodyProfileCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

/**
 * REST Controller for Physiological Analytics & Personal Record Tracking.
 * Exposes endpoints:
 * - GET /api/v1/analytics/profile (BMI, BMI Category, Goal Suggestions)
 * - GET /api/v1/analytics/prs (Historical Personal Records)
 * - POST /api/v1/analytics/prs/check (Check and save new PRs)
 */
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final UserRepository userRepository;
    private final PersonalRecordRepository prRepository;

    @Autowired
    public AnalyticsController(UserRepository userRepository, PersonalRecordRepository prRepository) {
        this.userRepository = userRepository;
        this.prRepository = prRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getPhysiologicalProfile(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        double bmi = BodyProfileCalculator.calculateBMI(user.getWeightKg(), user.getHeightCm());

        Map<String, Object> profile = new HashMap<>();
        profile.put("username", user.getUsername());
        profile.put("weightKg", user.getWeightKg());
        profile.put("heightCm", user.getHeightCm());
        profile.put("bmi", Math.round(bmi * 10.0) / 10.0);
        profile.put("bmiCategory", BodyProfileCalculator.getBMICategory(bmi));
        profile.put("suggestedGoal", BodyProfileCalculator.suggestGoal(bmi).name());
        profile.put("currentGoal", user.getFitnessGoal() != null ? user.getFitnessGoal().name() : "STRENGTH");
        profile.put("personalizationSummary", BodyProfileCalculator.getPersonalizationSummary(user));

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/prs")
    public ResponseEntity<List<PersonalRecord>> getUserPersonalRecords(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        List<PersonalRecord> prs = prRepository.findByUserIdOrderByAchievedDateDesc(user.getId());
        return ResponseEntity.ok(prs);
    }

    @PostMapping("/prs/check")
    public ResponseEntity<Map<String, Object>> checkAndSavePR(@RequestBody PrCheckRequest request, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        List<PersonalRecord> achievedPRs = new ArrayList<>();

        if (request.getWeightKg() > 0) {
            Optional<PersonalRecord> currentMax = prRepository.findTopByUserIdAndExerciseIdOrderByRecordValueDesc(
                    user.getId(), request.getExerciseId()
            );
            if (currentMax.isEmpty() || request.getWeightKg() > currentMax.get().getRecordValue()) {
                PersonalRecord newPR = new PersonalRecord(
                        user.getId(), request.getExerciseId(), request.getWeightKg(),
                        RecordType.MAX_WEIGHT, LocalDate.now()
                );
                newPR.setExerciseName(request.getExerciseName());
                prRepository.save(newPR);
                achievedPRs.add(newPR);
            }
        }

        if (request.getReps() > 0) {
            Optional<PersonalRecord> currentMaxReps = prRepository.findTopByUserIdAndExerciseIdOrderByRecordValueDesc(
                    user.getId(), request.getExerciseId()
            );
            if (currentMaxReps.isEmpty() || request.getReps() > currentMaxReps.get().getRecordValue()) {
                PersonalRecord newPR = new PersonalRecord(
                        user.getId(), request.getExerciseId(), request.getReps(),
                        RecordType.MAX_REPS, LocalDate.now()
                );
                newPR.setExerciseName(request.getExerciseName());
                prRepository.save(newPR);
                achievedPRs.add(newPR);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("newPrAchieved", !achievedPRs.isEmpty());
        result.put("achievedPRs", achievedPRs);
        return ResponseEntity.ok(result);
    }

    private User getUserFromAuth(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            User defaultUser = new User();
            defaultUser.setId(1);
            defaultUser.setUsername("demo_user");
            defaultUser.setWeightKg(75.0);
            defaultUser.setHeightCm(175.0);
            return defaultUser;
        }
        return userRepository.findByUsername(auth.getName())
                .or(() -> userRepository.findByEmail(auth.getName()))
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
