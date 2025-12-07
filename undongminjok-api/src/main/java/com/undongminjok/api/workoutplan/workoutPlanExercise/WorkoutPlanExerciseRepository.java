package com.undongminjok.api.workoutplan.workoutPlanExercise;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkoutPlanExerciseRepository
    extends JpaRepository<WorkoutPlanExercise, Long> {

  // 템플릿 1개에 속한 7일치 전체 조회
  @Query("""
      SELECT e
      FROM WorkoutPlanExercise e
      WHERE e.workoutPlan.template.id = :templateId
      ORDER BY e.day ASC, e.orderIndex ASC
      """)
  List<WorkoutPlanExercise> findAllByTemplateId(@Param("templateId") Long templateId);

  // 템플릿 + day 로 특정 일차만 조회
  @Query("""
      SELECT e
      FROM WorkoutPlanExercise e
      WHERE e.workoutPlan.template.id = :templateId
        AND e.day = :day
      ORDER BY e.orderIndex ASC
      """)
  List<WorkoutPlanExercise> findByTemplateIdAndDay(
      @Param("templateId") Long templateId,
      @Param("day") Integer day
  );
}
