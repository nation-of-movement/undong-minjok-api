package com.undongminjok.api.templates.domain;

import com.undongminjok.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommend",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "template_id"})
    })
@Getter
@NoArgsConstructor
public class TemplateRecommend {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 추천한 사용자
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // 추천 대상 템플릿
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "template_id", nullable = false)
  private Template template;

  public TemplateRecommend(User user, Template template) {
    this.user = user;
    this.template = template;
  }
}