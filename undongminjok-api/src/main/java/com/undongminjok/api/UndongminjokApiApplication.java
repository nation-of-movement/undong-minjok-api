package com.undongminjok.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UndongminjokApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UndongminjokApiApplication.class, args);
	}

}
