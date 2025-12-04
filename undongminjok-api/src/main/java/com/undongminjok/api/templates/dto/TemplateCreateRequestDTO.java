package com.undongminjok.api.templates.dto;

import com.undongminjok.api.workoutplan.WorkoutPlanExerciseDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateCreateRequestDTO {

  private String name;
  private String content;
  private String picture;
  private Long price;

  private List<WorkoutPlanExerciseDTO> exercises;

}
