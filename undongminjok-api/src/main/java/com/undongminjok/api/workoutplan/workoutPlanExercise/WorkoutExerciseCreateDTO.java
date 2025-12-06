package com.undongminjok.api.workoutplan.workoutPlanExercise;

import lombok.Getter;

@Getter
public class WorkoutExerciseCreateDTO {

  private Integer day;   // 1~7
  private String name;
  private Integer reps;
  private Integer weight;
  private Integer duration;
  private Integer orderIndex;

  // ⭐ 템플릿 단계에서도 어떤 기구인지 알고 싶으면 필요
  private Long equipmentId;   // nullable
}