package com.undongminjok.api.point.dto.response;


import com.undongminjok.api.point.dto.PointDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class PointDetailResponse {

  private PointDetailDTO pointDetailDTO;
  private Integer totalPoint;

}
