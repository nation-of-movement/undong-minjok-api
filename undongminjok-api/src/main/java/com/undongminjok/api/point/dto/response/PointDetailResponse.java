package com.undongminjok.api.point.dto.response;

import com.undongminjok.api.point.domain.PointStatus;
import com.undongminjok.api.point.dto.PointDetailDTO;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
@Builder
@AllArgsConstructor
@Getter
public class PointDetailResponse {

  private PointDetailDTO pointDetailDTO;
  private Integer totalPoint;
  private Integer sellingPoint;

}
