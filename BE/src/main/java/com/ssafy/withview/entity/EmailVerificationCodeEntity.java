package com.ssafy.withview.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@RedisHash(value = "emailVerificationCode", timeToLive = 180)
@Getter
@AllArgsConstructor
public class EmailVerificationCodeEntity {

	@Id
	private String email;
	private String code;
}
