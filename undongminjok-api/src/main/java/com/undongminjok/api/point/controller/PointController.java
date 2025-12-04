package com.undongminjok.api.point.controller;

import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.point.domain.PageType;
import com.undongminjok.api.point.domain.PointType;
import com.undongminjok.api.point.dto.PointDTO;
import com.undongminjok.api.point.dto.request.PointRequest;
import com.undongminjok.api.point.dto.response.PointResponse;
import com.undongminjok.api.point.service.PointService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
   * @param pointType, pageType
   * @return
   */
  @GetMapping("")
  public ResponseEntity<ApiResponse<PointResponse>> points(
      @RequestParam(required = false) PointType pointType,
      @RequestParam(required = true) PageType pageType) {

    List<PointDTO> response = pointService.getPoints(pointType, pageType);
    PointResponse pointResponse = PointResponse.builder().points(response).build();

    return ResponseEntity.ok(ApiResponse.success(pointResponse));
  }

}
