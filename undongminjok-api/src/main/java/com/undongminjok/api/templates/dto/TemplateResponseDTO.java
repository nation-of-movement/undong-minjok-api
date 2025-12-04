package com.undongminjok.api.templates.dto;

import com.undongminjok.api.global.dto.BaseTimeEntity;
import com.undongminjok.api.templates.domain.Template;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateResponseDTO {

  private Long userId;
  private Long id;
  private String name;
  private String content;
  private String picture;
  private Long price;
  private Long salesCount;
  private Long recommendCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static TemplateResponseDTO from(Template t) {
    return TemplateResponseDTO.builder()
        .id(t.getId())
        .name(t.getName())
        .content(t.getContent())
        .picture(t.getPicture())
        .price(t.getPrice())
        .salesCount(t.getSalesCount())
        .recommendCount(t.getRecommendCount())
        .createdAt(t.getCreatedAt())
        .updatedAt(t.getUpdatedAt())
        .build();
  }

}
