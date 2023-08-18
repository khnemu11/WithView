package com.ssafy.withview.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	private Long seq;
	private String name;
	private Integer limitPeople;
	private String backgroundImgSearchName;
	private String backgroundImgOriginalName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "server_seq")
	private ServerEntity serverEntity;

	public static ChannelDto toDto(ChannelEntity entity) {
		return ChannelDto.builder().seq(entity.getSeq())
			.name(entity.getName())
			.limitPeople(entity.getLimitPeople())
			.serverSeq(entity.getServerEntity().getSeq())
			.backgroundImgOriginalName(entity.getBackgroundImgOriginalName())
			.backgroundImgSearchName(entity.getBackgroundImgSearchName())
			.build();
	}

	public void update(ChannelDto channelDto) {
		this.backgroundImgSearchName = channelDto.getBackgroundImgSearchName();
		this.backgroundImgOriginalName = channelDto.getBackgroundImgOriginalName();
		this.name = channelDto.getName();
	}

	@Builder
	public ChannelEntity(String name, Integer limitPeople, String backgroundImgOriginalName,
		String backgroundImgSearchName, ServerEntity serverEntity) {
		this.name = name;
		this.limitPeople = limitPeople;
		this.backgroundImgOriginalName = backgroundImgOriginalName;
		this.backgroundImgSearchName = backgroundImgSearchName;
		this.serverEntity = serverEntity;
	}
}