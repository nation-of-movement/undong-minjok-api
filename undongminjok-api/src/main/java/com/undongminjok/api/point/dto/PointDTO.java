package com.undongminjok.api.point.dto;

import com.undongminjok.api.point.domain.PointType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
@Builder
@AllArgsConstructor
@Getter
public class PointDTO {

  private Long pointId;
  private Long templateId;
  private String templateName;
  private PointType pointType;
  private String paymentMethod;
  private Integer totalPoint;

  @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdDt;

}
