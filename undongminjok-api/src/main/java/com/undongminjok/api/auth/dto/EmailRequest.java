package com.undongminjok.api.auth.dto;

import com.undongminjok.api.auth.domain.VerificationPurpose;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailRequest {

  private String email;
  private VerificationPurpose purpose;
}
