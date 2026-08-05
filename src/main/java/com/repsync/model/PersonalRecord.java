package com.repsync.model;

import jakarta.persistence.*;
import com.repsync.model.enums.RecordType;
import java.time.LocalDate;

/**
 * Represents a Personal Record (PR) for a specific exercise.
 * PRs are automatically detected when a user logs a workout
 * with higher weight/reps/duration than their previous best.
 * Mapped to MySQL table "personal_records" via JPA.
 */
@Entity
@Table(name = "personal_records")
public class PersonalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "exercise_id", nullable = false)
    private int exerciseId;

    @Column(name = "record_value", nullable = false)
    private double recordValue;      // the PR value (kg, reps, or seconds)

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false)
    private RecordType recordType;

    @Column(name = "achieved_date")
    private LocalDate achievedDate;

    // For display purposes (Transient - not a column in personal_records table)
    @Transient
    private String exerciseName;

    public PersonalRecord() {
        this.achievedDate = LocalDate.now();
    }

    public PersonalRecord(int userId, int exerciseId, double recordValue,
                          RecordType recordType, LocalDate achievedDate) {
        this.userId = userId;
        this.exerciseId = exerciseId;
        this.recordValue = recordValue;
        this.recordType = recordType;
        this.achievedDate = achievedDate;
    }

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

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public double getRecordValue() {
        return recordValue;
    }

    public void setRecordValue(double recordValue) {
        this.recordValue = recordValue;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public LocalDate getAchievedDate() {
        return achievedDate;
    }

    public void setAchievedDate(LocalDate achievedDate) {
        this.achievedDate = achievedDate;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    /**
     * Get a formatted display string for this PR.
     */
    public String getFormattedValue() {
        return switch (recordType) {
            case MAX_WEIGHT -> recordValue + " kg";
            case MAX_REPS -> (int) recordValue + " reps";
            case MAX_DURATION -> (int) recordValue + " seconds";
        };
    }

    @Override
    public String toString() {
        return exerciseName + " - " + getFormattedValue() + " (" + achievedDate + ")";
    }
}
