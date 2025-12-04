package com.undongminjok.api.point.controller;

import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.point.dto.PointDTO;
import com.undongminjok.api.point.dto.request.PointRequest;
import com.undongminjok.api.point.dto.response.PointResponse;
import com.undongminjok.api.point.service.PointService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/v1/points")
public class PointController {

  private PointService pointService;

  /**
   * my point /selling point 조회/검색
   * @param pointRequest
   * @return
   */
  @GetMapping("")
  public ResponseEntity<ApiResponse<PointResponse>> points(PointRequest pointRequest) {

    List<PointDTO> response = pointService.getPoints(pointRequest);
    PointResponse pointResponse = PointResponse.builder().points(response).build();

    return ResponseEntity.ok(ApiResponse.success(pointResponse));
  }

}
