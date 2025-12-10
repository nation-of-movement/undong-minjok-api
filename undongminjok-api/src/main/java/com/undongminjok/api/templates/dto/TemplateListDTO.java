package com.undongminjok.api.templates.dto;

import com.undongminjok.api.templates.domain.Template;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemplateListDTO {

  private Long id;
  private String name;
  private String thumbnailImage;
  private Long price;

  public static TemplateListDTO from(Template t) {
    return TemplateListDTO.builder()
        .id(t.getId())
        .name(t.getName())
        .thumbnailImage(t.getThumbnailImage())
        .price(t.getPrice())
        .build();
  }
}
