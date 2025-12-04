package com.undongminjok.api.point.service;

import com.undongminjok.api.global.dto.LoginUserInfo;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.point.domain.PageType;
import com.undongminjok.api.point.domain.Point;
import com.undongminjok.api.point.domain.PointType;
import com.undongminjok.api.point.dto.PointDTO;
import com.undongminjok.api.point.dto.request.PointRequest;
import com.undongminjok.api.point.repository.PointRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

  private PointRepository pointRepository;

  /**
   * 포인트 목록 조회
   * @param pointRequest (point type)
   * @return
   */
  public List<PointDTO> getPoints(PointRequest pointRequest) {

    // 로그인 user
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();


    PointType pointType = PointType.valueOf(pointRequest.getPointType());
    PageType  pageType = PageType.valueOf(pointRequest.getPageType());

    return pointRepository.findPointDTOByUserIdAndPointType(userId, pointType, pageType);

//    return null;
  }

}
