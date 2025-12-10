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

    //로그인한 유저가 없으면 null값 반환
    //상세조회에 상세조회 + 추천이 엮여있어서, 로그인 한 사람과 안한사람 구별이 필요해서 넣음
    public Long getCurrentUserIdOrNull() {
        try {
            return getLoginUserInfo().getUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
