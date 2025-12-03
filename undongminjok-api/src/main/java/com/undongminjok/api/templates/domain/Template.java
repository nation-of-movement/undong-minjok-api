package com.undongminjok.api.templates.domain;

import com.undongminjok.api.global.dto.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Template extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "template_id")
  private Long id;

  // 대표 이미지 URL or 파일 경로
  @Column(name = "template_picture", nullable = false)
  private String Picture;

  // 추천(좋아요) 수 — 기본 0, 증가 로직에서 사용
  @Column(name = "recommend_count")
  private Long recommendCount;

  // 판매된 횟수 — 결제/구매 시 증가
  @Column(name = "sales_count")
  private Long salesCount;

  // 템플릿 이름(제목)
  @Column(name = "template_name", length = 50, nullable = false)
  private String Name;

  // 템플릿 내용(설명)
  @Column(name = "template_content", length = 255, nullable = false)
  private String Content;

  // 판매 가격
  @Column(name = "template_price", nullable = false)
  private Long Price;

// 작성자(회원) ID — FK(회원 테이블과 매핑될 값)
// @ManyToOne(fetch = FetchType.LAZY)
// @JoinColumn(name ="key")
// private Long key;

}
