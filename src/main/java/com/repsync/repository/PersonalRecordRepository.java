package com.repsync.repository;

import com.repsync.model.PersonalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for PersonalRecord entity.
 * Provides query methods for PR detection and historical PR listing.
 */
@Repository
public interface PersonalRecordRepository extends JpaRepository<PersonalRecord, Integer> {
    List<PersonalRecord> findByUserIdOrderByAchievedDateDesc(int userId);
    List<PersonalRecord> findByUserIdAndExerciseIdOrderByAchievedDateDesc(int userId, int exerciseId);
    Optional<PersonalRecord> findTopByUserIdAndExerciseIdOrderByRecordValueDesc(int userId, int exerciseId);
}
