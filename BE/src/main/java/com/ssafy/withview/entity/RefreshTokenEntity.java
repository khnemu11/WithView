package com.ssafy.withview.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@RedisHash(value = "refreshToken", timeToLive = 120)
@Getter
@AllArgsConstructor
public class RefreshTokenEntity {

	@Id
	private String seq;
	private String refreshToken;
}
