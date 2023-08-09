package com.ssafy.withview.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ssafy.withview.config.JwtProperties;
import com.ssafy.withview.constant.Role;
import com.ssafy.withview.dto.AccessTokenDto;
import com.ssafy.withview.dto.LoginDto;
import com.ssafy.withview.entity.LoginEntity;
import com.ssafy.withview.entity.RefreshTokenEntity;
import com.ssafy.withview.repository.LoginRepository;
import com.ssafy.withview.repository.RefreshTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	private final RefreshTokenRepository refreshTokenRepository;

	private final LoginRepository loginRepository;

	/**
	 * AccessToken 발급
	 *
	 * @param seq  (유저 pk 값)
	 * @param role (유저 권한)
	 * @return JwtDto (grantType, AccessToken)
	 */
	@Transactional(readOnly = true)
	public AccessTokenDto generateAccessToken(Long seq, Role role) {
		log.debug("JwtService - generateAccessToken 실행");

		String accessToken = JWT.create()
			.withSubject("AccessToken")
			.withIssuer("S09P12D208")
			.withAudience(String.valueOf(seq))
			.withExpiresAt(new Date(System.currentTimeMillis() + (JwtProperties.EXPIRATION_TIME_ACCESS)))
			.withClaim("role", role.toString())
			.sign(Algorithm.HMAC512(JwtProperties.SECRET_KEY.getBytes()));

		return AccessTokenDto.builder()
			.grantType(JwtProperties.TOKEN_PREFIX)
			.accessToken(accessToken)
			.build();
	}

	/**
	 * RefreshToken 발급 및 Redis 저장
	 *
	 * @param seq  (유저 pk 값)
	 * @param role (유저 권한)
	 * @return String (RefreshToken)
	 */
	@Transactional(readOnly = true)
	public String generateRefreshToken(Long seq, Role role) {
		log.debug("JwtService - generateRefreshToken  실행");

		String refreshToken = JWT.create()
			.withSubject("RefreshToken")
			.withIssuer("S09P12D208")
			.withAudience(String.valueOf(seq))
			.withExpiresAt(new Date(System.currentTimeMillis() + (JwtProperties.EXPIRATION_TIME_REFRESH)))
			.sign(Algorithm.HMAC512(JwtProperties.SECRET_KEY.getBytes()));

		refreshTokenRepository.save(new RefreshTokenEntity(String.valueOf(seq), refreshToken));
		log.debug("refreshToken redis 저장 완료");

		return refreshToken;
	}

	@Transactional
	public Authentication createAuthentication(Long seq, String password) {
		log.debug("JwtService - createAuthentication 실행");

		UsernamePasswordAuthenticationToken authenticationToken
			= new UsernamePasswordAuthenticationToken(String.valueOf(seq), password);

		log.debug("authenticationToken: {}", authenticationToken);

		// authenticate 매서드가 실행될 때 PrincipalDetailsService 에서 만든 loadUserByUsername 메서드가 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		log.debug("authentication: {} ", authentication);

		return authentication;
	}

	/**
	 * AccessToken 인증 정보 추출
	 *
	 * @param accessToken
	 * @return Authentication (인증 정보)
	 */
	public Authentication getAuthentication(String accessToken) {
		log.debug("JwtService - getAuthentication 실행");

		Claims claims = getClaims(accessToken);
		if (claims.get("role") == null) {
			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
		}

		log.debug("auth: {}", claims.get("role"));

		// 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get("role").toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		UserDetails principalDetails = new User(claims.getAudience(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principalDetails, "", authorities);
	}

	/**
	 * Token 검증
	 *
	 * @param token (AccessToken, RefreshToken)
	 * @return boolean (true: 정상적인 토큰, false: 만료된 토큰)
	 * @throws Exception (토큰이 손상됐을 때)
	 */
	public Boolean isValidToken(String token) {
		log.debug("JwtService - isValidToken 실행");
		try {
			Jwts.parserBuilder()
				.setSigningKey(JwtProperties.SECRET_KEY.getBytes())
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (MalformedJwtException e) {
			log.debug("손상된 토큰입니다", e);
			throw new JwtException("INVALID_TOKEN");
		} catch (UnsupportedJwtException e) {
			log.debug("지원하지 않는 토큰입니다", e);
			throw new JwtException("INVALID_TOKEN");
		} catch (IllegalArgumentException e) {
			log.debug("JWT claim이 비었습니다.");
			throw new JwtException("INVALID_TOKEN");
		} catch (SignatureException e) {
			log.debug("시그니처 검증에 실패했습니다.");
			throw new JwtException("INVALID_TOKEN");
		} catch (ExpiredJwtException e) {
			log.info("만료된 토큰입니다", e);
			return false;
		} catch (Exception e) {
			log.debug("기타 토큰 에러: {}", e.getMessage());
			throw new JwtException(e.getMessage());
		}
	}

	/**
	 * 토큰에서 Claim 추출
	 *
	 * @param token (AccessToken, RefreshToken)
	 * @return Claims
	 */
	public Claims getClaims(String token) {
		log.debug("JwtService - getClaims 실행");
		try {
			return Jwts.parserBuilder()
				.setSigningKey(JwtProperties.SECRET_KEY.getBytes())
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			log.info("만료된 토큰입니다", e);
			throw new JwtException("EXPIRED_TOKEN");
		}
	}

	/**
	 * Cookie 의 refreshToken 과 Redis 의 refreshToken 이 일치하는지 검사
	 *
	 * @param refreshToken (Cookie 의 refreshToken)
	 * @return Boolean (true: 일치, false: 불일치)
	 */
	public Boolean isSameToken(String refreshToken) {
		log.debug("JwtService - isSameToken 실행");

		Claims claims = getClaims(refreshToken);

		RefreshTokenEntity entity = refreshTokenRepository.findById(claims.getAudience())
			.orElseThrow(() -> new JwtException("EXPIRED_REFRESH_TOKEN"));

		if (entity.getRefreshToken().equals(refreshToken)) {
			return true;
		}
		return false;
	}

	/**
	 * Token 에 담겨있는 seq 로 유저 정보 가져오기
	 *
	 * @param token (RequestHeader 의 AccessToken 또는 Cookie 의 RefreshToken)
	 * @return LoginDto (userSeq, roles)
	 */
	public LoginDto getLoginInfo(String token) {
		log.debug("JwtService - getLoginInfo 실행");

		Claims claims = getClaims(token);

		LoginEntity loginEntity = loginRepository.findByUserSeq(Long.parseLong(claims.getAudience()))
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		return LoginDto.builder()
			.userSeq(loginEntity.getUserSeq())
			.roles(loginEntity.getRoles())
			.build();
	}

	/**
	 * Redis 에 저장된 RefreshToken 삭제
	 *
	 * @param seq (삭제 할 유저 pk 값)
	 */
	public void removeRefreshToken(Long seq) {
		log.debug("JwtService - removeRefreshToken 실행");
		refreshTokenRepository.deleteById(String.valueOf(seq));
	}

	/**
	 * 쿠키 생성 (Http only, secure)
	 *
	 * @param cookieName  (생성할 쿠키 이름)
	 * @param cookieValue (쿠키 값)
	 * @return ResponseCookie
	 */
	public ResponseCookie createCookie(String cookieName, String cookieValue) {
		log.debug("JwtService - createCookie 실행");
		return ResponseCookie.from(cookieName, cookieValue)
			.path("/")
			.sameSite("None")
			.httpOnly(true)
			.secure(true)
			.build();
	}

	/**
	 * 쿠키 삭제 (생성된 된 쿠키를 다시 넣으면 삭제가 된다.)
	 *
	 * @param cookieName (삭제할 쿠키 이름)
	 * @return ResponseCookie
	 */
	public ResponseCookie removeCookie(String cookieName) {
		log.debug("JwtService - removeCookie 실행");
		return ResponseCookie.from(cookieName, null)
			.path("/")
			.sameSite("None")
			.maxAge(0)
			.build();
	}

	/**
	 * AccessToken 추출 (Bearer prefix 제거)
	 *
	 * @param accessToken (Bearer prefix 가 포함된 accessToken)
	 * @return String (Bearer prefix 가 제거된 AccessToken)
	 */
	public String resolveAccessToken(String accessToken) {
		log.debug("JwtService - resolveAccessToken 실행");

		if (StringUtils.hasText(accessToken) && accessToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
			return accessToken.substring(7);
		}

		return "";
	}
}
