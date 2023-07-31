package com.ssafy.withview.repository.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class JwtDto {
	private String grantType;
	private String accessToken;
	private String refreshToken;
}
