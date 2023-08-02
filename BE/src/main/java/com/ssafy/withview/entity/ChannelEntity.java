package com.ssafy.withview.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ssafy.withview.dto.ChannelDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@ToString
@Table(name = "channel")
@NoArgsConstructor
public class ChannelEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;
	private String name;
	private int limitPeople;
	private int serverSeq;
	private String backgroundImgSearchName;
	private String backgroundImgOriginalName;

	public static ChannelDto toDto(ChannelEntity entity) {
		return ChannelDto.builder()
			.name(entity.getName())
			.limitPeople(entity.getLimitPeople())
			.serverSeq(entity.getServerSeq())
			.backgroundImgOriginalName(entity.getBackgroundImgOriginalName())
			.backgroundImgSearchName(entity.getBackgroundImgSearchName())
			.build();
	}

	public void update(ChannelDto channelDtoDto) {
		this.backgroundImgSearchName = channelDtoDto.getBackgroundImgOriginalName();
		this.backgroundImgOriginalName = channelDtoDto.getBackgroundImgOriginalName();
		this.name = channelDtoDto.getName();
	}

	@Builder
	public ChannelEntity(String name, int limitPeople, int serverSeq, String backgroundImgOriginalName,
		String backgroundImgSearchName) {
		this.name = name;
		this.limitPeople = limitPeople;
		this.serverSeq = serverSeq;
		this.backgroundImgOriginalName = backgroundImgOriginalName;
		this.backgroundImgSearchName = backgroundImgSearchName;
	}
}