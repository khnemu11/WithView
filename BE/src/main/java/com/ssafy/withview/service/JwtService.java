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
import com.ssafy.withview.repository.RefreshTokenRepository;
import com.ssafy.withview.dto.JwtDto;
import com.ssafy.withview.entity.RefreshTokenEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private final RefreshTokenRepository refreshTokenRepository;
	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

	public JwtDto generateToken(Authentication authentication) {
		return JwtDto.builder()
			.grantType(JwtProperties.TOKEN_PREFIX)
			.accessToken(generateAccessToken(authentication))
			.refreshToken(generateRefreshToken(authentication))
			.build();
	}

	@Transactional(readOnly = true)
	public String generateAccessToken(Authentication authentication) {

		logger.info("JwtService - generateAccessToken  실행");
		logger.info("Authentication: " + authentication);

		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		logger.info("authorities: " + authorities);

		return JWT.create()
			.withSubject("AccessToken")
			.withIssuer("S09P12D208")
			.withAudience(authentication.getName())
			.withExpiresAt(new Date(System.currentTimeMillis() + (JwtProperties.EXPIRATION_TIME_ACCESS)))
			.withClaim("auth", authorities)
			.sign(Algorithm.HMAC256(JwtProperties.SECRET_KEY.getBytes()));
	}

	@Transactional(readOnly = true)
	public String generateRefreshToken(Authentication authentication) {

		logger.info("JwtService - generateRefreshToken  실행");
		logger.info("Authentication: " + authentication);

		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		logger.info("authorities: " + authorities);

		String refreshToken = JWT.create()
			.withSubject("RefreshToken")
			.withIssuer("S09P12D208")
			.withAudience(authentication.getName())
			.withExpiresAt(new Date(System.currentTimeMillis() + (JwtProperties.EXPIRATION_TIME_REFRESH)))
			.sign(Algorithm.HMAC512(JwtProperties.SECRET_KEY.getBytes()));

		refreshTokenRepository.save(new RefreshTokenEntity(refreshToken, authentication.getName()));
		logger.info("[redis] refreshToken 저장 완료");

		return refreshToken;
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
	public Boolean isValidToken(String token) {
		logger.info("JwtService - isValidToken 실행");
		try {
			Jwts.parserBuilder().setSigningKey(JwtProperties.SECRET_KEY.getBytes()).build().parseClaimsJws(token);
			return true;
		} catch (MalformedJwtException e) {
			logger.info("손상된 토큰입니다", e);
			throw new JwtException("NON_LOGIN");
		} catch (ExpiredJwtException e) {
			logger.info("만료된 토큰입니다", e);
			throw new JwtException("EXPIRED_TOKEN");
		} catch (UnsupportedJwtException e) {
			logger.info("지원하지 않는 토큰입니다", e);
			throw new JwtException("NON_LOGIN");
		} catch (IllegalArgumentException e) {
			logger.info("JWT claim이 비었습니다.");
			throw new JwtException("NON_LOGIN");
		} catch (SignatureException e) {
			logger.info("시그니처 검증에 실패했습니다.");
			throw new JwtException("NON_LOGIN");
		}
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
			logger.info("만료된 토큰입니다", e);
			throw new JwtException("EXPIRED_TOKEN");
		}
	}

	public Boolean existsRefreshToken(String refreshToken) {
		logger.info("JwtService - existsRefreshToken 실행");

		Claims claims = getClaims(refreshToken);

		logger.info("audience: " + claims.getAudience());

		return refreshTokenRepository.existsById(claims.getAudience());
	}

	public String getRefreshToken(String id) {
		logger.info("JwtService - getRefreshToken 실행");
		RefreshTokenEntity entity = refreshTokenRepository.findById(id)
			.orElseThrow(() -> new JwtException("EXPIRED_TOKEN"));
		return entity.getRefreshToken();
	}

	public String getId(String refreshToken) {
		logger.info("JwtService - getId 실행");

		Claims claims = getClaims(refreshToken);
		return claims.getAudience();
	}
}
