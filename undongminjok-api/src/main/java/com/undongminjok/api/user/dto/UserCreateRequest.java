package com.undongminjok.api.user.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCreateRequest {

  private final String loginId;
  private final String name;
  private final String nickname;
  private final String password;
  private final String email;
  private final String resetToken;
}
