package com.undongminjok.api.templates.dto;

import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutPlanExerciseDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TemplateDetailDTO {

  private Long id;
  private String name;
  private String content;
  private Long price;

  private String thumbnailImage;
  private String templateImage;

  private Long salesCount;
  private Long recommendCount;

  private boolean recommended;
  private String writerNickname;

  private List<TemplateDayDTO> days;

  @Getter
  @Builder
  @AllArgsConstructor
  public static class TemplateDayDTO {
    private Integer day;
    private List<WorkoutPlanExerciseDTO> exercises;
  }

  public static TemplateDetailDTO of(Template t,
      boolean recommended,
      List<TemplateDayDTO> days) {

    return TemplateDetailDTO.builder()
        .id(t.getId())
        .name(t.getName())
        .content(t.getContent())
        .price(t.getPrice())
        .thumbnailImage(t.getThumbnailImage())
        .templateImage(t.getTemplateImage())
        .salesCount(t.getSalesCount())
        .recommendCount(t.getRecommendCount())
        .writerNickname(t.getUser().getNickname())
        .recommended(recommended)
        .days(days)
        .build();
  }
}
