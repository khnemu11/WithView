package com.ssafy.withview.repository.dto;

import com.ssafy.withview.repository.entity.ChannelEntity;
import com.ssafy.withview.repository.entity.ServerEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServerDto {
	private long seq;
	private String name;
	private int limitChannel=5;
	private int hostSeq;

	public static ServerEntity toEntity(ServerDto serverDto){
		return ServerEntity.builder()
			.seq(serverDto.getSeq())
			.name(serverDto.getName())
			.limitChannel(serverDto.getLimitChannel())
			.hostSeq(serverDto.getHostSeq()).build();
	}
}