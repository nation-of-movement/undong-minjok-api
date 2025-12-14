package com.undongminjok.api.point.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointStatus {


  RECHARGE("RECHARGE", "충전 완료"), PURCHASE("PURCHASE", "구매 완료"), EARN("EARN", "적립 완료")
/*  ,WITHDRAW_WAIT("WITHDRAW_WAIT" ,  "출금 대기중")
  ,WITHDRAW_FAIL("WITHDRAW_FAIL" ,  "출금 취소")*/, WITHDRAW("WITHDRAW", "출금 완료");

  private final String status;
  private final String statusName;


}
