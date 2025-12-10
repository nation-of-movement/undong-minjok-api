package com.undongminjok.api.point.service.provider;

import com.undongminjok.api.point.dto.PointHistoryDTO;
import com.undongminjok.api.user.domain.User;

public interface PointProviderService {

  void createPointHistory(PointHistoryDTO pointHistoryDTO);
  void createPointHistory(User user, PointHistoryDTO dto);
}
