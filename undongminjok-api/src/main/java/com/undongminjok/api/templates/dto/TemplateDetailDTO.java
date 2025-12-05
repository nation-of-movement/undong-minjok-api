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
  private String picture;
  private Long price;
  private Long salesCount;
  private Long recommendCount;

  private boolean recommended; // 현재 유저가 이 템플릿 추천했는지 여부
  private String writerNickname;
  private String exerciseName;

  private String createdAt;
  private String updatedAt;

  private List<WorkoutPlanExerciseDTO> exercises;


  public static TemplateDetailDTO from(Template t, boolean recommended, List<WorkoutPlanExerciseDTO> exercises) {
    return TemplateDetailDTO.builder()
        .id(t.getId())
        .name(t.getName())
        .content(t.getContent())
        .picture(t.getPicture())
        .price(t.getPrice())
        .salesCount(t.getSalesCount())
        .recommendCount(t.getRecommendCount())
        .writerNickname(t.getUser().getNickname())
        .recommended(recommended)
        .exercises(exercises)
        .build();
  }
}

