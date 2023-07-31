package com.ssafy.withview.repository.entity;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@RedisHash(value = "refreshToken", timeToLive = 60)
@Getter
@AllArgsConstructor
public class RefreshTokenEntity {

	@Id
	private String refreshToken;
	private String id;
}
