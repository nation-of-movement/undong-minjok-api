package com.undongminjok.api.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("운동의 민족 API")
            .description("""
                운동 템플릿 생성·수정·판매 및
                개인 운동 기록 관리를 위한 REST API 명세서
                """)
            .version("v1.0.0")
        );
  }
}

// swagger 버전입니다.
