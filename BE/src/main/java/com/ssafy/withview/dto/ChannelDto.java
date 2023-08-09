package com.ssafy.withview.dto;

import com.ssafy.withview.entity.ChannelEntity;

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
public class ChannelDto {
	private Long seq;
	private String name;
	private Integer limitPeople;
	private Long serverSeq;
	private String backgroundImgSearchName;
	private String backgroundImgOriginalName;

	public static ChannelEntity toEntity(ChannelDto dto){
		return ChannelEntity.builder()
			.name(dto.getName())
			.limitPeople(dto.getLimitPeople())
			.backgroundImgOriginalName(dto.getBackgroundImgOriginalName())
			.backgroundImgSearchName(dto.getBackgroundImgSearchName())
			.build();
	}
}