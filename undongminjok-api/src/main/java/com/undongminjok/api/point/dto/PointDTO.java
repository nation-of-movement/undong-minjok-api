package com.undongminjok.api.point.dto;

import com.undongminjok.api.point.domain.PointStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
@Builder
@AllArgsConstructor
@Getter
public class PointDTO {

  private Long pointId;
  private String templateName;
  private PointStatus pointStatus;
  private Integer amount;

  @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdDt;


}
