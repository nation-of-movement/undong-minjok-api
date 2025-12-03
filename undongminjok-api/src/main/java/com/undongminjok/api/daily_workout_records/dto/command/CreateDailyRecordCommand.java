package com.undongminjok.api.daily_workout_records.dto.command;

import com.undongminjok.api.daily_workout_exercises.dto.command.CreateExerciseCommand;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDailyRecordCommand {
  private LocalDate date;
  private List<CreateExerciseCommand> exercises;
  private String workoutImgPath;
}
