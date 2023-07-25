package com.ssafy.withview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//스프링 시큐리티 미완성으로 인한 오류 발생 제거
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@CrossOrigin("*")
public class WithviewApplication implements WebMvcConfigurer {
	public static void main(String[] args) {
		SpringApplication.run(WithviewApplication.class, args);
	}
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedMethods("*")
			.allowedHeaders("*")
			.allowedOriginPatterns("*")
			.allowCredentials(false);
	}
}
