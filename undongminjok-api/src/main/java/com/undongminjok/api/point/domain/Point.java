package com.undongminjok.api.point.domain;


import com.undongminjok.api.global.dto.BaseTimeEntity;
import com.undongminjok.api.point.dto.PointHistoryDTO;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "point_history")
@NoArgsConstructor
public class Point extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "point_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "template_id")
  private Template template;

  @Column(nullable = false, name = "point_status")
  @Enumerated(EnumType.STRING)
  private PointStatus status;

  @Column(nullable = false, name = "point_amount")
  private Integer amount;

  @Column(length = 30, name = "payment_method")
  private String method;

  private String bank;
  private String accountNumber;
  private String orderId;

  @Builder(access = AccessLevel.PRIVATE)
  public Point(User user, Template template, PointStatus status, Integer amount,
      String method, String bank, String accountNumber, String orderId) {
    this.user = user;
    this.template = template;
    this.status = status;
    this.amount = amount;
    this.method = method;
    this.bank = bank;
    this.accountNumber = accountNumber;
    this.orderId = orderId;
  }


  public static Point createPoint(PointHistoryDTO dto, User user, Template template) {
    return Point.builder()
                .user(user)
                .template(template)
                .status(dto.getStatus())
                .amount(dto.getAmount())
                .method(dto.getMethod())
                .accountNumber(dto.getAccountNumber())
                .bank(dto.getBank())
                .orderId(dto.getOrderId())
                .build();

  }
}
