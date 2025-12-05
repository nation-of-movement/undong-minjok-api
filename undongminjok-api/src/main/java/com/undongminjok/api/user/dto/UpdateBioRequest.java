package com.undongminjok.api.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateBioRequest {

  private final String bio;
}
