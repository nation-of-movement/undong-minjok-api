package com.undongminjok.api.global.util;

import com.undongminjok.api.auth.AuthErrorCode;
import com.undongminjok.api.global.dto.LoginUserInfo;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

  public static LoginUserInfo getLoginUserInfo() {
    Authentication authentication = SecurityContextHolder.getContext()
                                                         .getAuthentication();
    if (authentication == null || authentication.getPrincipal() == null) {
      throw new BusinessException(AuthErrorCode.UNAUTHORIZED_USER);
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof CustomUserDetails userDetails) {
      return new LoginUserInfo(userDetails.getUserId(), userDetails.getUsername(),
                               userDetails.getRole());
    }

    throw new BusinessException(AuthErrorCode.UNAUTHORIZED_USER);
  }
}
