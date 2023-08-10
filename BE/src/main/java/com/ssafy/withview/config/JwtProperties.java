package com.ssafy.withview.config;

public interface JwtProperties {
	String SECRET_KEY = "S09P12D208JwtSecretKeyS09P12D208JwtSecretKeyS09P12D208JwtSecretKeyS09P12D208JwtSecretKeyCrazySSAFYMonsterSecurityByHc5514";
	Integer EXPIRATION_TIME_ACCESS = 1000 * 60 * 60 * 4; // 4시간
	Integer EXPIRATION_TIME_REFRESH = 1000 * 60 * 60 * 24; // 24시간
	String TOKEN_PREFIX = "Bearer ";
	String HEADER_STRING_ACCESS = "Authorization";
}
