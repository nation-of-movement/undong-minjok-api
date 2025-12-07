package com.undongminjok.api.workoutplan;

import com.undongminjok.api.workoutplan.workoutPlan.WorkoutPlan;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
  Optional<WorkoutPlan> findByTemplateId(Long templateId);
  }