package com.undongminjok.api.daily_workout_exercises.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateExerciseRequest {
  private String exerciseName;
  private String part;
  private Integer reps;
  private Integer weight;
  private Integer duration;
  private Long equipmentId;
  private Integer orderIndex;

}

