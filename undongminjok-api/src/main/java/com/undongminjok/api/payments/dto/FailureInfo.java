package com.undongminjok.api.payments.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FailureInfo {

  private String code;
  private String message;

}
