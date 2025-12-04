package com.undongminjok.api.user.domain;

import com.undongminjok.api.global.dto.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;

  @Column(nullable = false, length = 30)
  private String loginId;

  @Column(nullable = false, length = 600)
  private String password;

  @Column(nullable = false, length = 30)
  private String nickname;

  @Column(length = 50)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status;

  @Column(nullable = false)
  private Integer amount;

  @Builder(access = AccessLevel.PRIVATE)
  private User(UserRole role,
      String loginId,
      String password,
      String nickname,
      String email,
      UserStatus status,
      Integer amount) {

    this.role = role;
    this.loginId = loginId;
    this.password = password;
    this.nickname = nickname;
    this.email = email;
    this.status = status;
    this.amount = amount;
  }

  public static User createUser(String loginId,
      String password,
      String nickname,
      String email) {

    return User.builder()
               .role(UserRole.USER)
               .loginId(loginId)
               .password(password)
               .nickname(nickname)
               .email(email)
               .status(UserStatus.ACTIVE)
               .amount(0)
               .build();
  }

  public void updatePassword(String encode) {
    this.password = encode;
  }
}
