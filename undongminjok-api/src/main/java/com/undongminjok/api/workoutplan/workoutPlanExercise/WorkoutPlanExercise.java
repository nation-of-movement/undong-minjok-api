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
@Builder
public class WorkoutPlanExercise {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "plan_exercise_id")
  private Long id;
    @Column(name = "plan_exercise_day")
  private Integer day;
    @Column(name = "plan_exercise_name")
  private String name;
    @Column(name = "plan_exercise_part")
    private String part;
    @Column(name = "plan_exercise_reps")
  private Integer reps;
    @Column(name = "plan_exercise_weight")
    private Integer weight;
    @Column(name = "plan_exercise_duration")
    private Integer duration;
    @Column(name = "plan_exercise_orderIndex")
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

  public void update(
      Integer day,
      String name,
      String part,
      Integer reps,
      Integer weight,
      Integer duration,
      Integer orderIndex,
      Equipment equipment
  ) {
    this.day = day;
    this.name = name;
    this.part = part;
    this.reps = reps;
    this.weight = weight;
    this.duration = duration;
    this.orderIndex = orderIndex;
    this.equipment = equipment;
  }

}
