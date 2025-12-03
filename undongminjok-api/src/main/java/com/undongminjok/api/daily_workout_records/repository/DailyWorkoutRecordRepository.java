package com.undongminjok.api.daily_workout_records.repository;

import com.undongminjok.api.daily_workout_records.domain.DailyWorkoutRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyWorkoutRecordRepository extends JpaRepository<DailyWorkoutRecord, Long> {

  Optional<DailyWorkoutRecord> findByUserUserIdAndDate(Long userId, LocalDate date);


}
