package com.undongminjok.api.templates.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.user.dto.UserProfileResponse;
import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutPlanExerciseDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TemplateDetailResponseDTO {

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

  private UserProfileResponse userProfile;

  @JsonProperty("isMine")
  private boolean isMine;

  @Getter
  @Builder
  @AllArgsConstructor
  public static class TemplateDayDTO {

    private Integer day;
    private List<WorkoutPlanExerciseDTO> exercises;
  }

  public static TemplateDetailResponseDTO of(Template t,
      boolean recommended,
      List<TemplateDayDTO> days,
      boolean isMine,
      UserProfileResponse userProfile) {

    return TemplateDetailResponseDTO.builder()
                                    .id(t.getId())
                                    .name(t.getName())
                                    .content(t.getContent())
                                    .price(t.getPrice())
                                    .thumbnailImage(t.getThumbnailImage())
                                    .templateImage(t.getTemplateImage())
                                    .salesCount(t.getSalesCount())
                                    .recommendCount(t.getRecommendCount())
                                    .writerNickname(t.getUser()
                                                     .getNickname())
                                    .recommended(recommended)
                                    .days(days)
                                    .isMine(isMine)
                                    .userProfile(userProfile)
                                    .build();
  }
}
