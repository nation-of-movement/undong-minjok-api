package com.undongminjok.api.user.dto;

import com.undongminjok.api.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {

  private String loginId;
  private String nickname;
  private String profileImagePath;

  public static UserProfileResponse from(User user) {
    return UserProfileResponse.builder()
                              .loginId(user.getLoginId())
                              .nickname(user.getNickname())
                              .profileImagePath(user.getProfileImagePath()) // 이미 만들었음
                              .build();
  }
}
