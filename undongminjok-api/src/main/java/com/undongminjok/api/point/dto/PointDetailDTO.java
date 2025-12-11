package com.undongminjok.api.point.dto;

import com.undongminjok.api.point.domain.PointStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@AllArgsConstructor
@Getter
public class PointDetailDTO {

  private Long pointId;
  private String templateName;
  private PointStatus pointStatus;
  private Long price;
  private Integer amount;
  private String paymentMethod;
  private String bank;
  private String accountNumber;

  @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdDt;

}
