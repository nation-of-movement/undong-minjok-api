package com.undongminjok.api.daily_workout_records.dto.request;

import com.undongminjok.api.daily_workout_exercises.dto.request.CreateExerciseRequest;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDailyRecordRequest {
  private LocalDate date;
  private List<CreateExerciseRequest> exercises;
  private String workoutImgPath;
}
