package com.ssafy.withview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableJpaAuditing
@SpringBootApplication()
public class WithviewApplication implements WebMvcConfigurer {
	public static void main(String[] args) {
		SpringApplication.run(WithviewApplication.class, args);
	}
}
