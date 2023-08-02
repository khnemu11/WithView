package com.ssafy.withview.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JoinDto {
	private String id;
	private String password;
	private String email;
	private String nickname;
}
