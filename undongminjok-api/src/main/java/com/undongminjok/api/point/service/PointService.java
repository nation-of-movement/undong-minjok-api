package com.undongminjok.api.point.service;

import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.point.PointErrorCode;
import com.undongminjok.api.point.domain.PaymentMethod;
import com.undongminjok.api.point.domain.PointStatus;
import com.undongminjok.api.point.dto.PointDTO;
import com.undongminjok.api.point.dto.PointDetailDTO;
import com.undongminjok.api.point.dto.PointHistoryDTO;
import com.undongminjok.api.point.dto.PointStatusDTO;
import com.undongminjok.api.point.dto.request.PointRefundRequest;
import com.undongminjok.api.point.dto.response.PointDetailResponse;
import com.undongminjok.api.point.dto.response.PointResponse;
import com.undongminjok.api.point.repository.PointRepository;
import com.undongminjok.api.point.service.provider.PointProviderService;
import com.undongminjok.api.user.UserErrorCode;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.service.provider.UserProviderService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

  private final PointRepository pointRepository;
  private final PointProviderService pointProviderService;
  private final UserProviderService userProviderService;

  /**
   * 포인트 목록, 포인트 구분, 총 포인트 조회
   * @param pointStatus
   * @return
   */
  public PointResponse getPointResponse (PointStatus pointStatus) {

    // userId
    Long userId = Optional.ofNullable(SecurityUtil.getLoginUserInfo().getUserId())
        .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    return PointResponse.builder()
        .points(getPoints(userId, pointStatus))
        .pointStatuses(getPointStatuses())
        .totalPoint(userProviderService.getUserAccount(userId))
        .sellingPoint(getSellingPoints(userId))
        .build();
  }


  /**
   * 포인트 목록 조회
   * @param  pointStatus, pageType
   * @return
   */
  private List<PointDTO> getPoints(Long userId, PointStatus pointStatus) {

    // point status 있는 경우
    if (pointStatus != null) {
      return pointRepository.findPointByPointStatus(userId, pointStatus);
    }

    return pointRepository.findMyPointAll(userId);
  }


  /**
   * 포인트 상세 조회
   * @param pointId
   * @return
   */
  public PointDetailResponse getPointDetail(Long pointId) {

    // userId
    Long userId = Optional.ofNullable(SecurityUtil.getLoginUserInfo().getUserId())
        .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    // 상세조회

    PointDetailDTO point = Optional.ofNullable(pointRepository.findMyPointByPointId(userId, pointId))
        .orElseThrow(() -> new BusinessException(PointErrorCode.POINT_HISTORY_NOT_FOUND));

    return PointDetailResponse.builder()
        .pointDetailDTO(point)
        .totalPoint(userProviderService.getUserAccount(userId))
        .build();

  }

  /**
   *
   * PointStatus enum class로 리스트 가져오기
   * @return
   */
  private List<PointStatusDTO> getPointStatuses () {

    return Arrays.stream(PointStatus.values())
        .map(status -> new PointStatusDTO(status.getStatus(), status.getStatusName()))
        .toList();
  }

  /**
   * Selling 포인트 총합
   * @param
   * @return
   */
  private Integer getSellingPoints (Long userId) {

    return pointRepository.findTotalSellingPoint(userId);
  }

  /**
  * 포인트 환불
  */
  @Transactional
  public void refundPoint(PointRefundRequest request) {
    Long userId = Optional.ofNullable(SecurityUtil.getLoginUserInfo().getUserId())
                          .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    User user = userProviderService.findByIdForUpdate(userId);

    if (user.getAmount() < request.getPoint()) {
      throw new BusinessException(PointErrorCode.POINT_NOT_ENOUGH);
    }

    PointHistoryDTO pointHistoryDTO =
                PointHistoryDTO.builder()
                               .userId(userId)
                               .status(PointStatus.WITHDRAW)
                               .method(PaymentMethod.BANK_TRANSFER.toString())
                               .amount(-request.getPoint())
                               .bank(request.getBank())
                               .orderId(null)
                               .accountNumber(request.getAccountNumber())
                               .build();

    pointProviderService.createPointHistory(user, pointHistoryDTO);
  }
}
