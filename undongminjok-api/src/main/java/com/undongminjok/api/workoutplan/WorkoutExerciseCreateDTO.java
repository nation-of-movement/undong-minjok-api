package com.undongminjok.api.workoutplan;

import lombok.Getter;

@Getter
public class WorkoutExerciseCreateDTO {

  private Integer day;   // 1~7
  private String name;
  private Integer reps;
  private Integer weight;
  private Integer duration;
  private Integer orderIndex;
}
