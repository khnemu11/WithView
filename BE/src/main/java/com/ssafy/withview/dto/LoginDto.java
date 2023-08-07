package com.ssafy.withview.dto;

import com.ssafy.withview.constant.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
	private String id;
	private String password;
	private Role roles;
	private Long userSeq;
}
