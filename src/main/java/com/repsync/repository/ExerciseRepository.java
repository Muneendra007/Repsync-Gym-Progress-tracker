package com.repsync.repository;

import com.repsync.model.Exercise;
import com.repsync.model.enums.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Exercise entity.
 * Supports filtering by exercise type (STRENGTH/CARDIO) and muscle group.
 */
@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    Optional<Exercise> findByName(String name);
    List<Exercise> findByExerciseType(ExerciseType exerciseType);
    List<Exercise> findByMuscleGroup(String muscleGroup);
}
