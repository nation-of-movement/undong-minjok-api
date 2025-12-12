package com.undongminjok.api;

import com.undongminjok.api.payments.config.TossPaymentProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
@EnableConfigurationProperties(TossPaymentProperties.class)
@SpringBootApplication
@EnableJpaAuditing
public class UndongminjokApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UndongminjokApiApplication.class, args);
	}

}
