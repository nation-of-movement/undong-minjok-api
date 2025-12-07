package com.undongminjok.api.workoutplan.workoutPlan;

import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutPlanExerciseDTO;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workout-plans")
@RequiredArgsConstructor
public class WorkoutPlanController {

  private final WorkoutPlanService workoutPlanService;

  // 템플릿 7일치 계획 저장
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public Long createPlan(@RequestBody WorkoutPlanCreateRequest request) {
    return workoutPlanService.saveWeeklyPlan(request);
  }

  // 템플릿 전체 7일치 조회
  @GetMapping("/template/{templateId}/weekly")
  @PreAuthorize("isAuthenticated()")
  public Map<Integer, List<WorkoutPlanExerciseDTO>> getWeeklyPlan(
      @PathVariable Long templateId
  ) {
    return workoutPlanService.getWeeklyPlan(templateId);
  }

  // 템플릿 특정 day 조회
  @GetMapping("/template/{templateId}/day/{day}")
  @PreAuthorize("isAuthenticated()")
  public List<WorkoutPlanExerciseDTO> getDayPlan(
      @PathVariable Long templateId,
      @PathVariable Integer day
  ) {
    return workoutPlanService.getDayPlan(templateId, day);
  }
}
