package com.ssafy.withview.config.filter;

public interface JwtProperties {

	String SECRET = "Monster";
	int EXPIRATION_TIME_ACCESS = 60000 * 60; // 60분
	int EXPIRATION_TIME_REFRESH = 60000 * 60 * 24 * 12; // 2주
	String TOKEN_PREFIX = "Bearer ";
	String HEADER_STRING = "Authorization";
}
