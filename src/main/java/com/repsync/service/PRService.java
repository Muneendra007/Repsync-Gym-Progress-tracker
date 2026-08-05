package com.repsync.service;

import com.repsync.dao.PersonalRecordDAO;
import com.repsync.model.PersonalRecord;
import com.repsync.model.WorkoutExercise;
import com.repsync.model.enums.RecordType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Personal Record (PR) detection and tracking.
 * Automatically checks if a logged exercise is a new PR.
 */
public class PRService {

    private final PersonalRecordDAO prDAO = new PersonalRecordDAO();

    /**
     * Check if a workout exercise is a new PR and save it if so.
     * Returns a list of new PRs that were achieved.
     * 
     * @param userId the user's ID
     * @param workoutExercise the exercise that was just logged
     * @return list of new PRs (may be empty if no new records)
     */
    public List<PersonalRecord> checkAndSavePRs(int userId, WorkoutExercise workoutExercise) {
        List<PersonalRecord> newPRs = new ArrayList<>();

        // Check for max weight PR
        if (workoutExercise.getWeightKg() > 0) {
            PersonalRecord currentPR = prDAO.getLatestPR(userId, workoutExercise.getExerciseId(), RecordType.MAX_WEIGHT);

            if (currentPR == null || workoutExercise.getWeightKg() > currentPR.getRecordValue()) {
                PersonalRecord newPR = new PersonalRecord(
                    userId, workoutExercise.getExerciseId(),
                    workoutExercise.getWeightKg(), RecordType.MAX_WEIGHT, LocalDate.now()
                );
                newPR.setExerciseName(workoutExercise.getExerciseName());
                prDAO.insert(newPR);
                newPRs.add(newPR);
            }
        }

        // Check for max reps PR
        if (workoutExercise.getReps() > 0) {
            PersonalRecord currentPR = prDAO.getLatestPR(userId, workoutExercise.getExerciseId(), RecordType.MAX_REPS);

            if (currentPR == null || workoutExercise.getReps() > currentPR.getRecordValue()) {
                PersonalRecord newPR = new PersonalRecord(
                    userId, workoutExercise.getExerciseId(),
                    workoutExercise.getReps(), RecordType.MAX_REPS, LocalDate.now()
                );
                newPR.setExerciseName(workoutExercise.getExerciseName());
                prDAO.insert(newPR);
                newPRs.add(newPR);
            }
        }

        // Check for max duration PR (for cardio)
        if (workoutExercise.getDurationSeconds() > 0) {
            PersonalRecord currentPR = prDAO.getLatestPR(userId, workoutExercise.getExerciseId(), RecordType.MAX_DURATION);

            if (currentPR == null || workoutExercise.getDurationSeconds() > currentPR.getRecordValue()) {
                PersonalRecord newPR = new PersonalRecord(
                    userId, workoutExercise.getExerciseId(),
                    workoutExercise.getDurationSeconds(), RecordType.MAX_DURATION, LocalDate.now()
                );
                newPR.setExerciseName(workoutExercise.getExerciseName());
                prDAO.insert(newPR);
                newPRs.add(newPR);
            }
        }

        return newPRs;
    }

    /**
     * Get all PRs for a user.
     */
    public List<PersonalRecord> getAllPRs(int userId) {
        return prDAO.findAllByUser(userId);
    }

    /**
     * Get PRs for a specific exercise.
     */
    public List<PersonalRecord> getPRsForExercise(int userId, int exerciseId) {
        return prDAO.findByUserAndExercise(userId, exerciseId);
    }
}
