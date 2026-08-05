package com.repsync.model;

import jakarta.persistence.*;
import com.repsync.model.enums.Difficulty;
import com.repsync.model.enums.ExerciseType;
import com.repsync.util.ExerciseGuideCatalog;

/**
 * Base Exercise class - represents any exercise in the system.
 * Includes anatomical sub-muscle targets, machine equipment setup,
 * biomechanical execution form guides, and illustration type.
 * 
 * Demonstrates: Encapsulation (private fields + getters/setters)
 * Extended by: StrengthExercise, CardioExercise (Inheritance)
 * Mapped to MySQL table "exercises" via JPA Single-Table Inheritance.
 */
@Entity
@Table(name = "exercises")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "exercise_type", discriminatorType = DiscriminatorType.STRING)
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type", insertable = false, updatable = false)
    private ExerciseType exerciseType;

    @Column(name = "muscle_group")
    private String muscleGroup;

    private String equipment;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private String description;

    // Anatomical and biomechanical guidance (Transient - populated from catalog)
    @Transient
    private String targetRegion;
    @Transient
    private String machineSetup;
    @Transient
    private String formGuide;
    @Transient
    private String illustrationType;

    @PostLoad
    public void onPostLoad() {
        if (this.exerciseType == null) {
            if (this instanceof StrengthExercise) {
                this.exerciseType = ExerciseType.STRENGTH;
            } else if (this instanceof CardioExercise) {
                this.exerciseType = ExerciseType.CARDIO;
            } else {
                this.exerciseType = ExerciseType.STRENGTH;
            }
        }
    }

    // Default constructor (required by JPA)
    public Exercise() {
    }

    // Constructor with essential fields
    public Exercise(String name, ExerciseType exerciseType, String muscleGroup) {
        this.name = name;
        this.exerciseType = exerciseType;
        this.muscleGroup = muscleGroup;
    }

    // Full constructor
    public Exercise(int id, String name, ExerciseType exerciseType, String muscleGroup,
                    String equipment, Difficulty difficulty, String description) {
        this.id = id;
        this.name = name;
        this.exerciseType = exerciseType;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.difficulty = difficulty;
        this.description = description;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // --- Anatomical and Machine Guide Getters with Catalog Fallback ---

    public String getTargetRegion() {
        if (targetRegion != null && !targetRegion.isEmpty()) return targetRegion;
        return ExerciseGuideCatalog.getGuide(name, muscleGroup).targetRegion;
    }

    public void setTargetRegion(String targetRegion) {
        this.targetRegion = targetRegion;
    }

    public String getMachineSetup() {
        if (machineSetup != null && !machineSetup.isEmpty()) return machineSetup;
        return ExerciseGuideCatalog.getGuide(name, muscleGroup).machineSetup;
    }

    public void setMachineSetup(String machineSetup) {
        this.machineSetup = machineSetup;
    }

    public String getFormGuide() {
        if (formGuide != null && !formGuide.isEmpty()) return formGuide;
        return ExerciseGuideCatalog.getGuide(name, muscleGroup).formGuide;
    }

    public void setFormGuide(String formGuide) {
        this.formGuide = formGuide;
    }

    public String getIllustrationType() {
        if (illustrationType != null && !illustrationType.isEmpty()) return illustrationType;
        return ExerciseGuideCatalog.getGuide(name, muscleGroup).illustrationType;
    }

    public void setIllustrationType(String illustrationType) {
        this.illustrationType = illustrationType;
    }

    @Override
    public String toString() {
        return name;  // Display just the name in UI dropdowns/lists
    }
}
