package com.ssafy.withview.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_server")
public class UserServerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_seq")
	private UserEntity userEntity;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "server_seq")
	private ServerEntity serverEntity;

	@Builder
	public UserServerEntity(UserEntity userEntity, ServerEntity serverEntity) {
		this.userEntity = userEntity;
		this.serverEntity = serverEntity;
	}
}