package com.undongminjok.api.templates.dto;

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
}

