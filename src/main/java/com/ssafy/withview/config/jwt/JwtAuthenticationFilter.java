package com.ssafy.withview.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ssafy.withview.service.JwtService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		logger.info("JwtAuthenticationFilter 실행");

		String token = resolveToken(request);
		logger.info("JWT: " + token);

		if (token != null && jwtService.isValidToken(token)) {
			// 토큰이 유효할 경우 SecurityContext 에 저장
			Authentication authentication = jwtService.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}

	// Request Header에서 jwt 추출
	private String resolveToken(HttpServletRequest request) {
		logger.info("JwtAuthenticationFilter - resolveToken 실행");
		String bearerToken = request.getHeader(JwtProperties.HEADER_STRING);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
