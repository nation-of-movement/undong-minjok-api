package com.undongminjok.api.daily_workout_records.dto.response;

import com.undongminjok.api.daily_workout_exercises.dto.response.ExerciseResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DailyRecordResponse {

  private Long recordId;
  private LocalDate date;
  private String workoutImg;
  private List<ExerciseResponse> exercises;
}
