package com.undongminjok.api.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDto {

  private int page = 0;                 // 0부터 시작
  private int size = 10;
  private String sort = "id";
  private Sort.Direction direction = Sort.Direction.DESC;

  public Pageable toPageable() {
    int safePage = Math.max(page, 0);
    int safeSize = size <= 0 ? 10 : size;

    return PageRequest.of(safePage, safeSize, Sort.by(direction, sort));
  }

  // MyBatis 같이 쓸 거면 이거 있으면 편함
  public int getOffset() {
    return Math.max(page, 0) * (size <= 0 ? 10 : size);
  }
}