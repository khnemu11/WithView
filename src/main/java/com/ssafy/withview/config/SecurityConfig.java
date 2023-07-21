package com.ssafy.withview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import com.ssafy.withview.config.jwt.JwtAuthorizationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsFilter corsFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable();
		// Jwt 토큰을 이용하기 때문에 비활성화
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.formLogin().disable()
			.httpBasic().disable();
		// Filter 추가
		http.addFilter(corsFilter);
			// .addFilter(new JwtAuthorizationFilter(authentica))
		// 모든 요청에 대해 사용자 인증
		http.authorizeRequests(authorize -> authorize
				.anyRequest().authenticated());
		return http.build();
	}
}
