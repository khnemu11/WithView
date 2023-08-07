package com.ssafy.withview.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ssafy.withview.constant.Role;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "login")
public class LoginEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;

	private String id;

	private String password;

	@Column(name = "roles")
	@Enumerated(EnumType.STRING)
	private Role roles;
	private Long userSeq;

	@Builder
	public LoginEntity(String id, String password, Role roles, Long userSeq) {
		this.id = id;
		this.password = password;
		this.roles = roles;
		this.userSeq = userSeq;
	}

	public void updatePassword(String password) {
		this.password = password;
	}
}
