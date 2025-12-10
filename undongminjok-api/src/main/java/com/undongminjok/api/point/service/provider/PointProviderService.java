package com.undongminjok.api.point.service.provider;

import com.undongminjok.api.point.dto.PointHistoryDTO;
import com.undongminjok.api.user.domain.User;

public interface PointProviderService {

  // 템플릿/유저 둘 다 DTO 안에 있는 경우 → 여기서 user 조회 + amount 증감까지 처리
  void createPointHistory(PointHistoryDTO pointHistoryDTO);

  // 이미 락 잡힌 User를 넘기는 경우 (출금처럼 템플릿 없이 쓰는 이력)
  void createPointHistory(User user, PointHistoryDTO dto);
}
