package com.undongminjok.api.daily_workout_exercises.repository;

import com.undongminjok.api.daily_workout_exercises.domain.DailyWorkoutExercise;
import com.undongminjok.api.daily_workout_records.domain.DailyWorkoutRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyWorkoutExerciseRepository extends JpaRepository<DailyWorkoutExercise, Long> {

  List<DailyWorkoutExercise> findByWorkoutRecordOrderByOrderIndexAsc(DailyWorkoutRecord record);

  void deleteByWorkoutRecord(DailyWorkoutRecord record);

  List<DailyWorkoutExercise> findByTemplateIdOrderByOrderIndexAsc(Long templateId);

  int countByWorkoutRecord(DailyWorkoutRecord record);
}
