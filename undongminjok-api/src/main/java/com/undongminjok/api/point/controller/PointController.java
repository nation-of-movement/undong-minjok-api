package com.undongminjok.api.point.controller;


import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.point.domain.PageType;
import com.undongminjok.api.point.domain.PointStatus;
import com.undongminjok.api.point.dto.PointDTO;
import com.undongminjok.api.point.dto.PointStatusDTO;
import com.undongminjok.api.point.dto.response.PointDetailResponse;
import com.undongminjok.api.point.dto.response.PointResponse;
import com.undongminjok.api.point.service.PointService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/v1/points")
public class PointController {

  private final PointService pointService;

  /**
   * my point /selling point 조회/검색
   * @param pointStatus, pageType
   * @return
   */
  @GetMapping("")
  public ResponseEntity<ApiResponse<PointResponse>> points(
      @RequestParam(required = false) PointStatus pointStatus,
      @RequestParam PageType pageType) {

    // 포인트 조회 목록
    List<PointDTO> points = pointService.getPoints(pointStatus, pageType);

    // 포인트 구분 목록
    List<PointStatusDTO> pointStatuses = pointService.getPointStatuses();

    // 총 포인트


    // 빌드
    PointResponse pointResponse = PointResponse.builder()
                  .points(points)
                  .pointStatuses(pointStatuses)
                  .build();

    return ResponseEntity.ok(ApiResponse.success(pointResponse));
  }

  /**
   * 포인트 상세 조회
   * @param pointId
   * @return
   */
  @GetMapping("/detail/{pointId}")
  public ResponseEntity<ApiResponse<PointDetailResponse>> points(
      @PathVariable Long pointId) {

    PointDetailResponse response = pointService.getPointDetail(pointId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

}
