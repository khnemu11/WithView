package com.ssafy.withview.service;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ssafy.withview.config.jwt.JwtProperties;
import com.ssafy.withview.repository.dto.JwtDto;
import com.ssafy.withview.repository.dto.LoginDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	// 유저 정보를 통해 JWT 생성
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

	// 토큰에서 인증 정보 추출
	public Authentication getAuthentication(String accessToken) {

	}

	// 토큰에서 Claim 추출
	public Claims getClaims(String accessToken) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(JwtProperties.SECRET_KEY)
				.build()
				.parseClaimsJws(accessToken)
				.getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}
}
