package com.undongminjok.api.point.controller;


import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.point.domain.PointStatus;
import com.undongminjok.api.point.dto.request.PointRefundRequest;
import com.undongminjok.api.point.dto.response.PointDetailResponse;
import com.undongminjok.api.point.dto.response.PointResponse;
import com.undongminjok.api.point.service.PointService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Point",
    description = "사용자 포인트 조회 및 출금 API"
)
@RestController
@RequiredArgsConstructor
@Slf4j
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
      @RequestParam(required = false) PointStatus pointStatus) {

    PointResponse response = pointService.getPointResponse(pointStatus);
    return ResponseEntity.ok(ApiResponse.success(response));
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

  /**
   * 포인트 출금
   */
  @PostMapping("/refund")
  public ResponseEntity<ApiResponse<Void>> refundPoints(
      @RequestBody PointRefundRequest request
  ) {

    pointService.refundPoint(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
