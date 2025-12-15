package com.undongminjok.api.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccessTokenResponse {

  private String accessToken;
}
