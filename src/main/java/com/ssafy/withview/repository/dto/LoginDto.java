package com.ssafy.withview.repository.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginDto {

	private String id;
	private String password;
	private String roles;
}
