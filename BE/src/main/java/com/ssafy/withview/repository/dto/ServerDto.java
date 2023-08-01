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
public class ServerDto {
	private long seq;
	private String name;
	private int limitChannel;
	private long hostSeq;
	private String backgroundImgSearchName;
	private String backgroundImgOriginalName;

	public static ServerEntity toEntity(ServerDto serverDto){
		if(serverDto == null){
			return null;
		}
		return ServerEntity.builder()
			.name(serverDto.getName())
			.limitChannel(serverDto.getLimitChannel())
			.backgroundImgSearchName(serverDto.getBackgroundImgSearchName())
			.backgroundImgOriginalName(serverDto.getBackgroundImgOriginalName())
			.hostSeq(serverDto.getHostSeq())
			.build();
	}

}