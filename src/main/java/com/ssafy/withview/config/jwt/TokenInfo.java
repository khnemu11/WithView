package com.ssafy.withview.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TokenInfo {
	private String grantType;
	private String accessToken;
	private String refreshToken;
}
