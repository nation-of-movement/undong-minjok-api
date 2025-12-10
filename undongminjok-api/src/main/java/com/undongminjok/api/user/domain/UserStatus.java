package com.undongminjok.api.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

  ACTIVE("ACTIVE", "활성화"),
  WITHDRAW("WITHDRAW", "탈퇴");


  private final String type;
  private final String typeName;
}
