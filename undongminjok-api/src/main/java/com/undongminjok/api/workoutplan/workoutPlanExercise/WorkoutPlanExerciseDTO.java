package com.undongminjok.api.workoutplan.workoutPlanExercise;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkoutPlanExerciseDTO {

  private Long exerciseId;
  private Integer day;
  private String name;
  private String part;
  private Integer reps;
  private Integer weight;
  private Integer duration;
  private Integer orderIndex;

  private Long equipmentId;
  private String equipmentName;

  public static WorkoutPlanExerciseDTO from(WorkoutPlanExercise ex) {
    return WorkoutPlanExerciseDTO.builder()
        .exerciseId(ex.getId())
        .day(ex.getDay())
        .name(ex.getName())
        .part(ex.getPart())
        .reps(ex.getReps())
        .weight(ex.getWeight())
        .duration(ex.getDuration())
        .orderIndex(ex.getOrderIndex())
        .equipmentId(ex.getEquipment() != null ? ex.getEquipment().getId() : null)
        .equipmentName(ex.getEquipment() != null ? ex.getEquipment().getName() : null)
        .build();
  }
}
