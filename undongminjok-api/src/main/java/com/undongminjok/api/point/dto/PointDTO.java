package com.undongminjok.api.point.dto;

import com.undongminjok.api.point.domain.PointType;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class PointDTO {

  private Long pointId;
  private Long templateId;
  private String templateName;
  private PointType pointType;
  private String paymentMethod;
  private Integer totalPoint;

  @DateTimeFormat(pattern ="yyyy년MM월dd일")
  private LocalDate createdDt;

}
