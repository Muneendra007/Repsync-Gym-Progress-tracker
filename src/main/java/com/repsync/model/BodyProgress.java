package com.repsync.model;

import java.time.LocalDate;

/**
 * Tracks a user's body measurements over time.
 * Used for the weight progress chart and BMI tracking.
 */
public class BodyProgress {

    private int id;
    private int userId;
    private double weightKg;
    private double bmi;
    private LocalDate recordDate;
    private String notes;

    public BodyProgress() {
        this.recordDate = LocalDate.now();
    }

    public BodyProgress(int userId, double weightKg, double bmi, LocalDate recordDate) {
        this.userId = userId;
        this.weightKg = weightKg;
        this.bmi = bmi;
        this.recordDate = recordDate;
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

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return recordDate + " - " + weightKg + " kg (BMI: " + String.format("%.1f", bmi) + ")";
    }
}
