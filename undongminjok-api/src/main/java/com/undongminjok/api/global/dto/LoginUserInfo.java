package com.undongminjok.api.global.dto;

import com.undongminjok.api.user.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginUserInfo {

  private final Long userId;
  private final String loginId;
  private final UserRole role;
}