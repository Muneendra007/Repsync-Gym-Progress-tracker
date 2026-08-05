package com.repsync.model;

import com.repsync.model.enums.UserRole;

/**
 * Trainer user - extends User with trainer-specific fields.
 * 
 * Demonstrates: Inheritance (Trainer IS-A User)
 * Trainers can create workout plans for other users.
 */
public class Trainer extends User {

    private String specialty;       // e.g., "Strength Training", "Yoga"
    private String certification;   // e.g., "NASM Certified"

    public Trainer() {
        super();
        setRole(UserRole.TRAINER);
    }

    public Trainer(String username, String password, String email) {
        super(username, password, email);
        setRole(UserRole.TRAINER);
    }

    // --- Getters and Setters ---

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    /**
     * Check if trainer can create workout plans for users.
     */
    public boolean canCreatePlansForUsers() {
        return true;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", specialty='" + specialty + '\'' +
                '}';
    }
}
