package com.ssafy.withview.repository.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtDto {
	private String grantType;
	private String accessToken;
	private String refreshToken;
}
