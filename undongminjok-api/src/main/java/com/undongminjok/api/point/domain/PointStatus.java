package com.undongminjok.api.point.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointStatus {


  RECHARGE("RECHARGE" , "RECHARGE", "충전완료")
  ,PURCHASE("PURCHASE" , "PURCHASE", "구매완료")
  ,REFUND_WAIT("REFUND_WAIT" , "REFUND", "환불 대기중")
  ,REFUND("REFUND" , "REFUND", "환불 완료")

  ,EARN("EARN" , "SALE", "적립 완료")
  ,WITHDRAW_WAIT("WITHDRAW" , "WITHDRAW", "출금 대기중")
  ,WITHDRAW("WITHDRAW" , "WITHDRAW", "출금 완료")
  ;

  private final String status;
  private final String type;
  private final String statusName;


}
