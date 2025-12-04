package com.undongminjok.api.point.dto.response;


import com.undongminjok.api.point.dto.PointDTO;
import java.util.List;
import lombok.Builder;

@Builder
public class PointResponse {

  private List<PointDTO> points;

}
