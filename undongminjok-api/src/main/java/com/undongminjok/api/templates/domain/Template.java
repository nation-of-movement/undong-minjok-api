package com.undongminjok.api.templates.domain;

import com.undongminjok.api.global.dto.BaseTimeEntity;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.workoutplan.workoutPlan.WorkoutPlan;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "templates")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Template extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "template_id")
  private Long id;

  @Column(name = "thumbnail_image", nullable = false)
  private String thumbnailImage;

  @Column(name = "template_image", nullable = false)
  private String templateImage;

  @Enumerated(EnumType.STRING)
  @Column(name = "template_status", nullable = false)
  private TemplateStatus status;   // ⭐ 그대로 유지

  @Column(name = "recommend_count")
  private Long recommendCount;

  @Column(name = "sales_count")
  private Long salesCount;

  @Column(name = "template_name", length = 50, nullable = false)
  private String name;

  @Column(name = "template_content", length = 255, nullable = false)
  private String content;

  @Column(name = "template_price", nullable = false)
  private Long price;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToOne(fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "plan_id")
  private WorkoutPlan workoutPlan;

  @Builder
  public Template(String thumbnailImage,
      String templateImage,
      String name,
      String content,
      Long price,
      User user,
      TemplateStatus status) {

    this.thumbnailImage = thumbnailImage;
    this.templateImage = templateImage;
    this.name = name;
    this.content = content;
    this.price = price;
    this.user = user;
    this.status = (status != null ? status : (price > 0 ? TemplateStatus.PAID : TemplateStatus.FREE));

    this.recommendCount = 0L;
    this.salesCount = 0L;
  }

  public void setWorkoutPlan(WorkoutPlan plan) {
    this.workoutPlan = plan;
  }

  public void update(String content, Long price) {
    this.content = content;
    this.price = price;
  }

  public void updateThumbnail(String path) {
    this.thumbnailImage = path;
  }

  public void updateTemplateImage(String path) {
    this.templateImage = path;
  }

  public void increaseSalesCount() { this.salesCount++; }
  public void increaseRecommend() { this.recommendCount++; }
  public void decreaseRecommend() { this.recommendCount--; }

  //  추가된 soft delete (판매된 템플릿 보호)
  public void softDelete() {
    this.status = TemplateStatus.STOPPED;
  }
}
