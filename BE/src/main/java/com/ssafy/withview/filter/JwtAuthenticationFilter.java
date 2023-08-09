package com.ssafy.withview.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ssafy.withview.config.JwtProperties;
import com.ssafy.withview.dto.AccessTokenDto;
import com.ssafy.withview.dto.LoginDto;
import com.ssafy.withview.service.JwtService;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		log.debug("JwtAuthenticationFilter 실행");

		String accessToken = resolveAccessToken(request);
		log.debug("AccessToken: {}", accessToken);

		// AccessToken 검사
		if (accessToken != null) {
			// AccessToken 이 정상일 때
			if (jwtService.isValidToken(accessToken)) {
				Authentication authentication = jwtService.getAuthentication(accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			// AccessToken 이 정상이지만, 만료가 되었다면 RefreshToken 검사
			else {
				// Cookie 에 RefreshToken 있는지 확인
				String refreshToken = "";
				try {
					for (int i = 0; i < request.getCookies().length; i++) {
						if (request.getCookies()[i].getName().equals("RefreshToken")) {
							refreshToken = request.getCookies()[i].getValue();
						}
					}
				} catch (Exception e) {
					throw new JwtException("EXPIRED_REFRESH_TOKEN");
				}
				// RefreshToken 이 정상인지 확인
				if (jwtService.isValidToken(refreshToken)) {
					log.debug("RefreshToken 검증 완료: {}", refreshToken);
					// Redis 에 저장된 RefreshToken 과 일치 여부 확인: AccessToken 재발급
					if (jwtService.isSameToken(refreshToken)) {
						LoginDto loginDto = jwtService.getLoginInfo(refreshToken);
						AccessTokenDto accessTokenDto = jwtService.generateAccessToken(loginDto.getUserSeq(),
							loginDto.getRoles());
						response.setHeader("Grant-Type", accessTokenDto.getGrantType());
						response.setHeader("New-Access-Token", accessTokenDto.getAccessToken());
						log.info("AccessToken 재발급 seq: {}", loginDto.getUserSeq());
						throw new JwtException("REISSUE_ACCESS_TOKEN");
					}
				}
				// RefreshToken 이 만료됐다면, 로그아웃 진행: 쿠키 삭제
				else {
					response.setHeader("Set-Cookie", jwtService.removeCookie("RefreshToken").toString());
					log.info("RefreshToken 검증 실패 or 만료됨. 로그아웃 진행");
					throw new JwtException("EXPIRED_REFRESH_TOKEN");
				}
			}
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * AccessToken 추출 (Bearer prefix 제거)
	 *
	 * @param request
	 * @return String (AccessToken)
	 */
	private String resolveAccessToken(HttpServletRequest request) {
		log.debug("JwtAuthenticationFilter - resolveAccessToken 실행");

		String bearerToken = request.getHeader(JwtProperties.HEADER_STRING_ACCESS);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
			return bearerToken.substring(7);
		}

		return null;
	}
}
