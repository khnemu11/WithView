package com.ssafy.withview.repository.entity;

import lombok.*;
import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name="user_server")
public class UserServerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_seq")
	private UserEntity userEntity;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="server_seq")
	private ServerEntity serverEntity;

	@Builder
	public UserServerEntity(UserEntity userEntity, ServerEntity serverEntity) {
		this.userEntity = userEntity;
		this.serverEntity = serverEntity;
	}
}