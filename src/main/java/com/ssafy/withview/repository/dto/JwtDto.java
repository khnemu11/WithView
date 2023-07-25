package com.ssafy.withview.repository.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JwtDto {

	private String grantType;
	private String accessToken;
	private String refreshToken;
}
