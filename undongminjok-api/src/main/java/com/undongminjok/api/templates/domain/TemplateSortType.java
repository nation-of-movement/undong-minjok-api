package com.undongminjok.api.templates.domain;

public enum TemplateSortType {

  // 추천 많은 순
  RECOMMEND,

  // 판매 많은 순
  SALES,

  // 최신 등록 순
  LATEST;

  // 문자열 파라미터 → Enum 매핑용 (잘못 들어오면 기본 LATEST)
  public static TemplateSortType from(String value) {
    if (value == null) {
      return LATEST;
    }
    try {
      return TemplateSortType.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      return LATEST;
    }
  }
}