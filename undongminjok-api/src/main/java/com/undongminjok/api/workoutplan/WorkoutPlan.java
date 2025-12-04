package com.undongminjok.api.workoutplan;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_plans")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 7일 운동 저장 리스트
  @OneToMany(mappedBy = "workoutPlan",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<WorkoutPlanExercise> exercises = new ArrayList<>();

  public void addExercise(WorkoutPlanExercise ex) {
    exercises.add(ex);
    ex.setWorkoutPlan(this);
  }
}