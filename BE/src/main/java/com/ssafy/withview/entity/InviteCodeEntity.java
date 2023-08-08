package com.ssafy.withview.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.ssafy.withview.dto.ServerDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@RedisHash(value = "secretCode", timeToLive = 600)
@Getter
@AllArgsConstructor
public class InviteCodeEntity {
	@Id
	private String secretCode;
	private ServerDto serverDto;
}
