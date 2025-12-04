package com.undongminjok.api.point.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointType {

  RECHARGE ("RECHARGE" , "MY" , "충전")
  , PURCHASE("PURCHASE", "MY", "구매")
  , REFUND("REFUND", "MY", "환불")
  , SALE("SALE", "SELLING", "판매")
  , WITHDRAW("WITHDRAW", "SELLING", "출금");


  private String type;
  private String pageType;
  private String typeName;



}
