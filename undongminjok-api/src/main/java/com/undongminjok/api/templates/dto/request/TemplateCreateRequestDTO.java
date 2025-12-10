package com.undongminjok.api.templates.dto.request;

import com.undongminjok.api.templates.domain.TemplateStatus;
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
  private Long price;
  private TemplateStatus status;

  private List<ExerciseCreateDTO> exercises;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ExerciseCreateDTO {
    private Integer day;
    private String name;
    private String part;
    private Integer reps;
    private Integer weight;
    private Integer duration;
    private Integer orderIndex;
    private Long equipmentId;
  }
}
