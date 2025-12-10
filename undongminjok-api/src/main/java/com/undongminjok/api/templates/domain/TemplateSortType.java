package com.undongminjok.api.templates.domain;

public enum TemplateSortType {

  RECOMMEND,  // 추천 많은 순
  SALES,  // 판매 많은 순
  LATEST;  // 최신 등록 순

  // 문자열 파라미터 → Enum 매핑 (잘못된 값은 예외 던짐)
  public static TemplateSortType from(String value) {

    if (value == null || value.isBlank()) {
      return LATEST;
    }

    switch (value.toUpperCase()) {
      case "LATEST":
        return LATEST;
      case "SALES":
        return SALES;
      case "RECOMMEND":
        return RECOMMEND;

      default:
        throw new IllegalArgumentException(
            "정렬(sort) 값이 잘못되었습니다. 사용 가능한 값: RECOMMEND, SALES, LATEST"
        );
    }
  }
}