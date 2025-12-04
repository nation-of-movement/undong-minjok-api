package com.undongminjok.api.point.dto.request;

import com.undongminjok.api.point.domain.PageType;
import com.undongminjok.api.point.domain.PointType;
import lombok.Getter;

@Getter
public class PointRequest {

  private PointType pointType;
  private PageType pageType;

}
