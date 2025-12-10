package com.undongminjok.api.workoutplan.workoutPlanExercise;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkoutPlanExerciseDTO {

  private Integer day;
  private String name;
  private String part;
  private Integer reps;
  private Integer weight;
  private Integer duration;
  private Integer orderIndex;

  public static WorkoutPlanExerciseDTO from(WorkoutPlanExercise ex) {
    return WorkoutPlanExerciseDTO.builder()
        .day(ex.getDay())
        .name(ex.getName())
        .part(ex.getPart())
        .reps(ex.getReps())
        .weight(ex.getWeight())
        .duration(ex.getDuration())
        .orderIndex(ex.getOrderIndex())
        .build();
  }
}
