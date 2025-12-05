package com.undongminjok.api.point.service;

import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.point.PointErrorCode;
import com.undongminjok.api.point.domain.PageType;
import com.undongminjok.api.point.domain.PointStatus;
import com.undongminjok.api.point.dto.PointDTO;
import com.undongminjok.api.point.dto.PointStatusDTO;
import com.undongminjok.api.point.dto.response.PointDetailResponse;
import com.undongminjok.api.point.repository.PointRepository;
import com.undongminjok.api.user.UserErrorCode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

  private final PointRepository pointRepository;

  /**
   * 포인트 목록 조회
   * @param  pointStatus, pageType
   * @return
   */
  public List<PointDTO> getPoints(PointStatus pointStatus, PageType pageType) {
    // 로그인 user
    Long userId = Optional.ofNullable(SecurityUtil.getLoginUserInfo().getUserId())
                        .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    List<PointDTO> response = Optional.ofNullable(pointStatus)
        .map(type -> pointRepository.findPointByPointStatus(userId, pointStatus.getStatus()))
        .orElseGet(() -> {
          if (pageType == null) {
            return Collections.emptyList();
          }

          switch (pageType) {
            case PageType.MY :
              return pointRepository.findMyPointAll(userId);
            case PageType.SELLING:
              return  pointRepository.findSellingPointAll(userId);
            default:
              return Collections.emptyList();
          }
        });
    return response;
  }


  /**
   * 포인트 상세 조회
   * @param pointId
   * @return
   */
  public PointDetailResponse getPointDetail(Long pointId) {

    Long userId = Optional.ofNullable(SecurityUtil.getLoginUserInfo().getUserId())
                          .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    // 상세조회
    return Optional.ofNullable(pointRepository.findMyPointByPointId(userId,pointId))
        .orElseThrow(() -> new BusinessException(PointErrorCode.POINT_HISTORY_NOT_FOUND));

  }

  /**
   *
   * PointStatus enum class로 리스트 가져오기
   * @return
   */
  public List<PointStatusDTO> getPointStatuses () {

    return Arrays.stream(PointStatus.values())
        .map(status -> new PointStatusDTO(status.getStatus(), status.getStatusName()))
        .toList();
  }

  /**
   * My/Selling 포인트 총합
   * @param userId
   * @param pageType
   * @return
   */
  public Integer getTotalPoints (Long userId, PageType pageType) {
    return null;
  }
}
