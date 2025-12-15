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
public class TemplateUpdateRequestDTO {

  private String content;          // 설명
  private Long price;              // 가격
  private TemplateStatus status;   // 상태 (null이면 서버에서 자동 계산 or 유지)

  private List<ExerciseUpdateDTO> exercises;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ExerciseUpdateDTO {

    private Long exerciseId;   // 기존 운동이면 값 있음, 새로 추가되는 운동이면 null

    private Integer day;
    private String name;
    private String part;
    private Integer reps;
    private Integer weight;
    private Integer duration;
    private Integer orderIndex;
    private Long equipmentId;

    private boolean deleted;   // true면 삭제
  }
}
