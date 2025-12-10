package com.undongminjok.api.workoutplan.workoutPlanExercise;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutPlanExerciseRepository extends JpaRepository<WorkoutPlanExercise, Long> {

    List<WorkoutPlanExercise> findByWorkoutPlanTemplateIdOrderByDayAscOrderIndexAsc(Long templateId);
}
