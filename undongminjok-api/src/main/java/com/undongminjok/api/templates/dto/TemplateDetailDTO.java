package com.undongminjok.api.templates.dto;

import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.workoutplan.WorkoutPlanExerciseDTO;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemplateDetailDTO {

  private Long id;
  private String name;
  private String content;
  private Long price;

  private String thumbnailImage;   // 추가
  private String templateImage;    // 추가

  private Long salesCount;
  private Long recommendCount;

  private boolean recommended;
  private String writerNickname;

  private String createdAt;
  private String updatedAt;

  private List<WorkoutPlanExerciseDTO> exercises;

  public static TemplateDetailDTO from(Template t, boolean recommended, List<WorkoutPlanExerciseDTO> exercises) {
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
        .createdAt(t.getCreatedAt().toString())
        .updatedAt(t.getUpdatedAt().toString())
        .exercises(exercises)
        .build();
  }
}

