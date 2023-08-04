package com.ssafy.withview.config.jwt;

public interface JwtProperties {

	String SECRET_KEY = "S09P12D208JwtSecretKeyCrazySSAFYMonsterSecurityByHc5514";
	Integer EXPIRATION_TIME_ACCESS = 10000; // 60분
	Integer EXPIRATION_TIME_REFRESH = 60000 * 60 * 24 * 12; // 2주
	String TOKEN_PREFIX = "Bearer ";
	String HEADER_STRING_ACCESS = "Authorization";
	String HEADER_STRING_REFRESH = "REFRESH-TOKEN";
}
