package com.undongminjok.api.workoutplan.workoutPlanExercise;

import com.undongminjok.api.equipments.domain.Equipment;
import com.undongminjok.api.workoutplan.workoutPlan.WorkoutPlan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workout_plan_exercises")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanExercise {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer day;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String part;

  private Integer reps;
  private Integer weight;
  private Integer duration;
  private Integer orderIndex;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "equipment_id")
  private Equipment equipment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plan_id")
  private WorkoutPlan workoutPlan;

  public void setWorkoutPlan(WorkoutPlan plan) {
    this.workoutPlan = plan;
  }
}