package com.undongminjok.api.templates.domain;

import com.undongminjok.api.daily_workout_exercises.domain.DailyWorkoutExercise;
import com.undongminjok.api.global.dto.BaseTimeEntity;
import com.undongminjok.api.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "templates")
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
  private String picture;

  // 추천(좋아요) 수 — 기본 0, 증가 로직에서 사용
  @Column(name = "recommend_count")
  private Long recommendCount;

  // 판매된 횟수 — 결제/구매 시 증가
  @Column(name = "sales_count")
  private Long salesCount;

  // 템플릿 이름(제목)
  @Column(name = "template_name", length = 50, nullable = false)
  private String name;

  // 템플릿 내용(설명)
  @Column(name = "template_content", length = 255, nullable = false)
  private String content;

  // 판매 가격
  @Column(name = "template_price", nullable = false)
  private Long price;

// 작성자(회원) ID — FK(회원 테이블과 매핑될 값)
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name ="user_id")
 private User user;

/*  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name ="exercise_id", nullable = false)
  private DailyWorkoutExercise dailyWorkoutExercise;*/

  @Builder
  public Template(String picture,
      String name,
      String content,
      Long price,
      User user) {

    this.picture = picture;
    this.name = name;
    this.content = content;
    this.price = price;
    this.user = user;

    // ⭐ 기본값 자동 설정
    this.recommendCount = 0L;
    this.salesCount = 0L;

  }

  // ===============================
  // ⭐ 수정 도메인 메서드 (Setter 대신)
  // ===============================
  public void update(String picture,
      String content,
      Long price) {

    this.picture = picture;
    this.content = content;
    this.price = price;

  }

  // ⭐ 추천 증가 로직
  public void increaseRecommend() {
    this.recommendCount++;
  }

  // ⭐ 판매 증가 로직
  public void increaseSales() {
    this.salesCount++;
  }

  public void decreaseRecommend() {
    this.recommendCount--;
  }
}
