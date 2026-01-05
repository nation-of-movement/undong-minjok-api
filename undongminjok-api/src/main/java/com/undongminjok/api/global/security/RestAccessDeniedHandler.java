package com.undongminjok.api.global.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/*
* 인증은 됐지만 권한이 없을 경우 사용하는 핸들러 > 로그인을 했지만, 권한이 없을 경우 응답값 설정
* */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    String jsonResponse =
        "{\"error\": \"Forbidden\", \"message\": \"" + accessDeniedException.getMessage() + "\"}";
    response.getWriter()
        .write(jsonResponse);
  }
}