package com.undongminjok.api.user.dto;

import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.domain.UserRole;
import com.undongminjok.api.user.domain.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {

  private String loginId;
  private String nickname;
  private String email;
  private String profileImagePath;
  private String bio;
  private Integer amount;

  public static UserInfoResponse from(User user) {
    return UserInfoResponse.builder()
                           .loginId(user.getLoginId())
                           .nickname(user.getNickname())
                           .email(user.getEmail())
                           .profileImagePath(user.getProfileImagePath())
                           .bio(user.getBio())
                           .amount(user.getAmount())
                           .build();
  }
}
