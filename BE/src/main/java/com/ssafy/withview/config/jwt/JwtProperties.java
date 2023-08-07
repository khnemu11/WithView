package com.ssafy.withview.config.jwt;

public interface JwtProperties {
	String SECRET_KEY = "S09P12D208JwtSecretKeyS09P12D208JwtSecretKeyS09P12D208JwtSecretKeyS09P12D208JwtSecretKeyCrazySSAFYMonsterSecurityByHc5514";
	Integer EXPIRATION_TIME_ACCESS = 1000 * 120; // 2분
	Integer EXPIRATION_TIME_REFRESH = 1000 * 300; // 5분
	String TOKEN_PREFIX = "Bearer ";
	String HEADER_STRING_ACCESS = "Authorization";
}
