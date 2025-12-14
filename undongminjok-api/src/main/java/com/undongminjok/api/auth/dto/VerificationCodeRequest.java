package com.undongminjok.api.auth.dto;

import com.undongminjok.api.auth.domain.VerificationPurpose;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerificationCodeRequest {

  private String email;
  private String code;
  private VerificationPurpose purpose;
}
