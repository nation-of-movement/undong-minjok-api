package com.undongminjok.api.point.dto.request;

import lombok.Getter;

@Getter
public class PointRequest {

  private String pointType;
  private String pageType;  // My point page (MY) / Selling point page (SELLING)

}
