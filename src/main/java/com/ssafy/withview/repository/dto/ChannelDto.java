package com.ssafy.withview.repository.dto;

import com.ssafy.withview.repository.entity.ChannelEntity;

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
public class ChannelDto {
	private long seq;
	String name;
	int limitPeople;
	int serverSeq;

	public static ChannelEntity toEntity(ChannelDto dto){
		return ChannelEntity.builder()
			.name(dto.getName())
			.limitPeople(dto.getLimitPeople())
			.serverSeq(dto.getServerSeq()).build();
	}
}