package com.undongminjok.api.global.security;

import com.undongminjok.api.user.domain.UserRole;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/*
* UserDetails를 상속 받아서 Custom화한 클래스
* 기존 UserDetails보다 더 많은 내용을 담기위해 사용
* */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

  private final Long userId;
  private final String loginId;
  private final String password;
  private final UserRole role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return loginId;
  }


}
