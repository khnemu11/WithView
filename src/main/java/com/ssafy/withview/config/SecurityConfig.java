package com.ssafy.withview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.ssafy.withview.config.jwt.JwtAuthenticationFilter;
import com.ssafy.withview.service.JwtService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsFilter corsFilter;

	private final JwtService jwtService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable();
		// 시큐리티는 기본적으로 세션을 사용
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			// httpBasic -> Bearer(Authorizatoin key의 value에 id, pw를 암호화한 토큰을 들고 요청)
			.formLogin().disable()
			.httpBasic().disable();

		http.addFilter(corsFilter)
			.addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

		http.authorizeRequests()
			.antMatchers("/login").permitAll()
			.antMatchers("/join").permitAll()
			.anyRequest().authenticated();

		return http.build();
	}
}
