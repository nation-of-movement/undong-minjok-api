package com.undongminjok.api.point.dto.request;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PointRefundRequest {

  private final Integer point;
  private final String bank;
  private final String accountNumber;
}
