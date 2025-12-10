package com.undongminjok.api.auth.dto;

import lombok.Getter;

@Getter
public class ResetPasswordRequest {

  private String resetToken;
  private String newPassword;
}

