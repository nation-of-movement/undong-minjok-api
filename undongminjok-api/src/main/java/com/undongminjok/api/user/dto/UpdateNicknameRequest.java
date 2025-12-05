package com.undongminjok.api.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateNicknameRequest {

  private final String nickname;
}
