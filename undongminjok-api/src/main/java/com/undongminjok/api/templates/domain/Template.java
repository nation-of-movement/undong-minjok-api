package com.undongminjok.api.templates.domain;


import com.undongminjok.api.global.dto.BaseTimeEntity;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.workoutplan.WorkoutPlan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "templates")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Template extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String picture;   // 기존 구조 유지 (기본 템플릿 이미지)

  // ⭐ 추가된 필드들
  private String thumbnailImage;   // 템플릿 리스트용 이미지(썸네일)
  private String templateImage;    // 상세 템플릿 미리보기 이미지

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private Long price;

  private Long recommendCount;
  private Long salesCount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  // ⭐ 템플릿이 사용하는 운동 계획 저장
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "plan_id")
  private WorkoutPlan workoutPlan;

  @Builder
  public Template(String picture, String name, String content, Long price, User user) {
    this.picture = picture;
    this.name = name;
    this.content = content;
    this.price = price;
    this.user = user;
    this.recommendCount = 0L;
    this.salesCount = 0L;
  }

  public void setWorkoutPlan(WorkoutPlan plan) {
    this.workoutPlan = plan;
  }

  // 기존 업데이트 로직 그대로 유지
  public void update(String picture, String content, Long price) {
    this.picture = picture;
    this.content = content;
    this.price = price;
  }

  // ⭐ 신규 이미지 업데이트용 메서드 2개 추가
  public void updateThumbnail(String path) {
    this.thumbnailImage = path;
  }

  public void updateTemplateImage(String path) {
    this.templateImage = path;
  }

  public void increaseRecommend() { this.recommendCount++; }
  public void decreaseRecommend() { if (this.recommendCount > 0) this.recommendCount--; }
  public void increaseSales() { this.salesCount++; }
}
