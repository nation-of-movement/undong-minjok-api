package com.undongminjok.api.global.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MailType {

  VERIFICATION("이메일 인증");

  private final String title;
}
