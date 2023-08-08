package com.ssafy.withview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.ssafy.withview.filter.JwtAuthenticationFilter;
import com.ssafy.withview.filter.JwtExceptionFilter;
import com.ssafy.withview.interceptor.JwtAccessDeniedHandler;
import com.ssafy.withview.service.JwtService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtService jwtService;

	private final CorsFilter corsFilter;

	private final JwtExceptionFilter jwtExceptionFilter;

	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable();
		// 시큐리티는 기본적으로 세션을 사용
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			// httpBasic -> Bearer(Authorization key 의 value 에 id, pw를 암호화한 토큰을 들고 요청)
			.formLogin().disable()
			.httpBasic().disable();

		http.exceptionHandling()
			.accessDeniedHandler(jwtAccessDeniedHandler);

		http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

		http.authorizeRequests()
			.antMatchers("/api/login/login").permitAll()
			.antMatchers("/api/users").permitAll()
			.antMatchers("/api/login/cookie").permitAll()
			.anyRequest().authenticated();

		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
