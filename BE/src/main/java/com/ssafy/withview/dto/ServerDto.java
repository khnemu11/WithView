package com.ssafy.withview.dto;

import com.ssafy.withview.entity.ServerEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerDto {
	private Long seq;
	private String name;
	private Integer limitChannel;
	private Long hostSeq;
	private String backgroundImgSearchName;
	private String backgroundImgOriginalName;
	private Boolean favorite;

	public static ServerEntity toEntity(ServerDto serverDto) {
		if (serverDto == null) {
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