package com.repsync.model;

import jakarta.persistence.*;
import com.repsync.model.enums.ExperienceLevel;
import com.repsync.model.enums.FitnessGoal;
import com.repsync.model.enums.UserRole;
import java.time.LocalDateTime;

/**
 * Base User class - represents a registered user in the system.
 * 
 * Demonstrates: Encapsulation (private fields + getters/setters)
 * Extended by: Admin, Trainer (Inheritance)
 * Mapped to MySQL table "users" via JPA.
 */
@Entity
@Table(name = "users")
public class User {

    // Private fields (Encapsulation)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;     // Stored as BCrypt hash

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private int age;
    private String gender;       // MALE, FEMALE, OTHER

    @Column(name = "height_cm")
    private double heightCm;

    @Column(name = "weight_kg")
    private double weightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "fitness_goal")
    private FitnessGoal fitnessGoal;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Default constructor (required by JPA)
    public User() {
        this.role = UserRole.USER;
    }

    // Constructor with essential fields
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = UserRole.USER;
    }

    // Full constructor
    public User(int id, String username, String password, String email, UserRole role,
                int age, String gender, double heightCm, double weightKg,
                FitnessGoal fitnessGoal, ExperienceLevel experienceLevel, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.age = age;
        this.gender = gender;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.fitnessGoal = fitnessGoal;
        this.experienceLevel = experienceLevel;
        this.createdAt = createdAt;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public FitnessGoal getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(FitnessGoal fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Check if the user has completed their body profile setup.
     * Used to trigger the onboarding wizard for new users.
     */
    public boolean isProfileComplete() {
        return heightCm > 0 && weightKg > 0 && age > 0 && fitnessGoal != null;
    }

    /**
     * Check if this user has admin privileges.
     */
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    /**
     * Check if this user is a trainer.
     */
    public boolean isTrainer() {
        return this.role == UserRole.TRAINER;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
