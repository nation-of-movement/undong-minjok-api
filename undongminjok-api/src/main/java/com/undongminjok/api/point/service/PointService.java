package com.undongminjok.api.point.service;

import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.point.domain.PageType;
import com.undongminjok.api.point.domain.PointType;
import com.undongminjok.api.point.dto.PointDTO;
import com.undongminjok.api.point.dto.request.PointRequest;
import com.undongminjok.api.point.repository.PointRepository;
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
   * @param  pointType, pageType
   * @return
   */
  public List<PointDTO> getPoints(PointType pointType, PageType pageType) {
    // 로그인 user
    //Long userId = SecurityUtil.getLoginUserInfo().getUserId();
    Long userId = 1L;


    return Optional.ofNullable(pointType)
        .map(type -> pointRepository.findPointByPointType(userId,type))
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
  }

}
