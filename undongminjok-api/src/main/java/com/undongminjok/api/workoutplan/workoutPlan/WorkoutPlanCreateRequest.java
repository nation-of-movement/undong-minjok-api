package com.undongminjok.api.workoutplan.workoutPlan;

import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutExerciseCreateDTO;
import java.util.List;
import lombok.Getter;

@Getter
public class WorkoutPlanCreateRequest {

  private Long templateId; // 템플릿 기반 계획일 경우
  private List<WorkoutExerciseCreateDTO> exercises; // day 1~7 데이터 포함
}
