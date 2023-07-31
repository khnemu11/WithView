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

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		logger.info("JwtAuthenticationFilter 실행");

		String accessToken = resolveAccessToken(request);
		String refreshToken = resolveRefreshToken(request);
		logger.info("JWT accessToken: " + accessToken);
		logger.info("JWT refreshToken: " + refreshToken);

		// 1. RT 검사
		// 없다면, AT 검사
		// 있다면, redis 에 저장되어 있는 토큰과 비교
		// 1. 만료되었는지 확인
		// 2. 만료가 안된 토큰이라면, 유저 정보 추출해서 redis 들고와서 값 비교
		// 3. 값이 일치한다면 AT 발급

		if (refreshToken == null) {
			if (accessToken != null && jwtService.isValidToken(accessToken)) {
				// 토큰이 유효할 경우 SecurityContext 에 저장
				Authentication authentication = jwtService.getAuthentication(accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} else {
			if (jwtService.isValidToken(refreshToken)) {
				if (jwtService.existsRefreshToken(refreshToken)) {
					// redis 값 비교
					if (refreshToken.equals(jwtService.getRefreshToken(jwtService.getId(refreshToken)))) {
						// accessToken 발급

					} else {
						// refreshToken 정보 불일치
						logger.info("손상된 토큰입니다");
						throw new JwtException("NON_LOGIN");
					}
				} else {
					logger.info("만료된 refreshToken 입니다");
					throw new JwtException("EXPIRED_TOKEN");
				}
			}
		}
		filterChain.doFilter(request, response);
	}

	private String resolveAccessToken(HttpServletRequest request) {
		logger.info("JwtAuthenticationFilter - resolveAccessToken 실행");

		String bearerToken = request.getHeader(JwtProperties.HEADER_STRING_ACCESS);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private String resolveRefreshToken(HttpServletRequest request) {
		logger.info("JwtAuthenticationFilter - resolveRefreshToken 실행");

		String bearerToken = request.getHeader(JwtProperties.HEADER_STRING_REFRESH);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
