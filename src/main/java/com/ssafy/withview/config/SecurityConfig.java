package com.ssafy.withview.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.CorsFilter;

import com.ssafy.withview.config.filter.JwtAuthenticationFilter;
import com.ssafy.withview.config.filter.JwtAuthorizationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CorsFilter corsFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		// 시큐리티는 기본적으로 세션을 사용
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			// httpBasic -> Bearer(Authorizatoin key의 value에 id, pw를 암호화한 토큰을 들고 요청)
			.formLogin().disable()
			.httpBasic().disable();
		// Filter 추가
		http.addFilter(corsFilter)
			.addFilter(new JwtAuthenticationFilter(authenticationManager()))
			.addFilter(new JwtAuthorizationFilter(authenticationManager()));
		// 모든 요청에 대해 사용자 인증
		// http.authorizeRequests(authorize -> authorize
		// 	.anyRequest().authenticated());

	}

}
