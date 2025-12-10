package com.undongminjok.api.templates.dto.response;

import com.undongminjok.api.templates.domain.Template;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemplateListResponseDTO {

  private Long id;
  private String name;
  private String thumbnailImage;
  private Long price;

  public static TemplateListResponseDTO from(Template t) {
    return TemplateListResponseDTO.builder()
        .id(t.getId())
        .name(t.getName())
        .thumbnailImage(t.getThumbnailImage())
        .price(t.getPrice())
        .build();
  }
}
