package com.undongminjok.api.templates.dto.response;

import com.undongminjok.api.templates.domain.Template;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemplateListResponseDTO {

  private Long id;
  private String name;
  private String thumbnailImage;
  private Long price;
  private LocalDateTime createdAt;
  private Long recommendCount;
  private Long salesCount;
  private String writerNickname;

  public static TemplateListResponseDTO from(Template t) {
    return TemplateListResponseDTO.builder()
        .id(t.getId())
        .name(t.getName())
        .thumbnailImage(t.getThumbnailImage())
        .price(t.getPrice())
        .createdAt(t.getCreatedAt())
        .recommendCount(t.getRecommendCount())
        .salesCount(t.getSalesCount())
        .writerNickname(t.getUser().getNickname())
        .build();
  }
}

