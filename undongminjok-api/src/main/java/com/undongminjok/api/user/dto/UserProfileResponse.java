package com.undongminjok.api.user.dto;

import com.undongminjok.api.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {

  private String nickname;
  private String profileImagePath;
  private String bio;

  public static UserProfileResponse from(User user) {
    return UserProfileResponse.builder()
                              .bio(user.getBio())
                              .nickname(user.getNickname())
                              .profileImagePath(user.getProfileImagePath()) // 이미 만들었음
                              .build();
  }
}
