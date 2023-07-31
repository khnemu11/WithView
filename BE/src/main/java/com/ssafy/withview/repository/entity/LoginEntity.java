package com.ssafy.withview.repository.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity(name = "login")
public class LoginEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int seq;
	private String id;
	private String password;
	private String roles;

	@Builder
	public LoginEntity(String id, String password, String roles) {
		this.id = id;
		this.password = password;
		this.roles = roles;
	}

	public List<String> getRoleList() {
		if (this.roles.length() > 0) {
			return Arrays.asList(this.roles.split(","));
		}
		return new ArrayList<>();
	}
}
