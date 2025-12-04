package com.undongminjok.api.point.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointType {

  RECHARGE ("RECHARGE" , "충전")
  , PURCHASE("PURCHASE", "구매")
  , REFUND("REFUND", "환불")
  , SALE("SALE", "판매")
  , WITHDRAW("WITHDRAW", "출금");


  private String type;
  private String typeName;



}
