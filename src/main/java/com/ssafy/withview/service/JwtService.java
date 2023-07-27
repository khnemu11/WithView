package com.ssafy.withview.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ssafy.withview.config.jwt.JwtProperties;
import com.ssafy.withview.repository.dto.JwtDto;
import com.ssafy.withview.repository.dto.LoginDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

	// 유저 정보를 통해 JWT 생성
	@Transactional(readOnly = true)
	public JwtDto generateToken(LoginDto loginDto) {

		String accessToken = JWT.create()
			.withSubject(loginDto.getId())
			.withExpiresAt(new Date(System.currentTimeMillis() + (JwtProperties.EXPIRATION_TIME_ACCESS)))
			.withClaim("iss", "S09P12D208")
			.withClaim("sub", "AccessToken")
			.withClaim("aud", loginDto.getId())
			.withClaim("exp", JwtProperties.EXPIRATION_TIME_ACCESS)
			.sign(Algorithm.HMAC512(JwtProperties.SECRET_KEY));

		String refreshToken = JWT.create()
			.withSubject(loginDto.getId())
			.withExpiresAt(new Date(System.currentTimeMillis() + (JwtProperties.EXPIRATION_TIME_REFRESH)))
			.withClaim("iss", "S09P12D208")
			.withClaim("sub", "RefreshToken")
			.withClaim("aud", loginDto.getId())
			.withClaim("exp", JwtProperties.EXPIRATION_TIME_REFRESH)
			.sign(Algorithm.HMAC512(JwtProperties.SECRET_KEY));

		return JwtDto.builder()
			.grantType(JwtProperties.TOKEN_PREFIX)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	@Transactional(readOnly = true)
	public JwtDto generateToken2(Authentication authentication) {

		logger.info("JwtService - generateToken2 실행");
		logger.info("Authentication: " + authentication);

		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		logger.info("authorities: " + authorities);

		String accessToken = JWT.create()
			.withSubject("AccessToken")
			.withIssuer("S09P12D208")
			.withAudience(authentication.getName())
			.withExpiresAt(new Date(System.currentTimeMillis() + (JwtProperties.EXPIRATION_TIME_ACCESS)))
			.withClaim("auth", authorities)
			.sign(Algorithm.HMAC256(JwtProperties.SECRET_KEY.getBytes()));

		String refreshToken = JWT.create()
			.withSubject("RefreshToken")
			.withIssuer("S09P12D208")
			.withAudience(authentication.getName())
			.withExpiresAt(new Date(System.currentTimeMillis() + (JwtProperties.EXPIRATION_TIME_REFRESH)))
			.sign(Algorithm.HMAC512(JwtProperties.SECRET_KEY.getBytes()));

		return JwtDto.builder()
			.grantType(JwtProperties.TOKEN_PREFIX)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	// 토큰에서 인증 정보 추출
	public Authentication getAuthentication(String accessToken) {
		logger.info("JwtService - getAuthentication 실행");

		Claims claims = getClaims(accessToken);

		logger.info("auth: " + claims.get("auth"));

		if (claims.get("auth") == null) {
			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
		}

		// 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get("auth").toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		UserDetails principalDetails = new User(claims.getAudience(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principalDetails, "", authorities);
	}

	// 토큰 검증
	public boolean isValidToken(String token) {
		logger.info("JwtService - isValidToken 실행");
		try {
			Jwts.parserBuilder().setSigningKey(JwtProperties.SECRET_KEY.getBytes()).build().parseClaimsJws(token);
			return true;
		} catch (MalformedJwtException e) {
			logger.info("손상된 토큰입니다", e);
		} catch (ExpiredJwtException e) {
			logger.info("만료된 토큰입니다", e);
		} catch (UnsupportedJwtException e) {
			logger.info("지원하지 않는 토큰입니다", e);
		} catch (IllegalArgumentException e) {
			logger.info("JWT claim이 비었습니다.");
		} catch (SignatureException e) {
			logger.info("시그니처 검증에 실패했습니다.");
		}
		return false;
	}

	// 토큰에서 Claim 추출
	public Claims getClaims(String accessToken) {
		logger.info("JwtService - getClaims 실행");
		try {
			return Jwts.parserBuilder()
				.setSigningKey(JwtProperties.SECRET_KEY.getBytes())
				.build()
				.parseClaimsJws(accessToken)
				.getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}
}
