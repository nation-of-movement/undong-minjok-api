package com.undongminjok.api.user.service.provider;

import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.dto.UserProfileResponse;

public interface UserProviderService {

  // user가 가지고 있는 총 금액 조회
  Integer getUserAccount(Long userId);

  // user 정보 조회
  User getUser(Long userId);

  // user 락 조회
  User findByIdForUpdate(Long userId);

  UserProfileResponse getUserProfile(Long userId);
}
