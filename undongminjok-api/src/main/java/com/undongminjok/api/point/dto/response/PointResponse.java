package com.undongminjok.api.point.dto.response;


import com.undongminjok.api.point.dto.PointDTO;
import com.undongminjok.api.point.dto.PointStatusDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PointResponse {

  private List<PointDTO> points;
  private List<PointStatusDTO> pointStatuses;
  private Integer totalPoint;
  private Integer sellingPoint;


}
