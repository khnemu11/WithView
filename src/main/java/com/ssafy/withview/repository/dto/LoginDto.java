package com.ssafy.withview.repository.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginDto {
	private String id;
	private String password;
	private String roles;
}
