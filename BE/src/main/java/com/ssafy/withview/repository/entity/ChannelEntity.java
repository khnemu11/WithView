package com.ssafy.withview.repository.entity;

import javax.persistence.*;

import com.ssafy.withview.repository.dto.ChannelDto;

import lombok.AllArgsConstructor;
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
	private int limitPeople=5;
	private int serverSeq;
	private String backgroundImgSearchName;
	private String backgroundImgOriginalName;

	public static ChannelDto toDto(ChannelEntity entity){
		return ChannelDto.builder()
			.name(entity.getName())
			.limitPeople(entity.getLimitPeople())
			.serverSeq(entity.getServerSeq()).build();
	}
	@Builder
	public ChannelEntity(String name, int limitPeople, int serverSeq,String backgroundImgOriginalName, String backgroundImgSearchName) {
		this.name = name;
		this.limitPeople = limitPeople;
		this.serverSeq = serverSeq;
		this.backgroundImgOriginalName = backgroundImgOriginalName;
		this.backgroundImgSearchName = backgroundImgSearchName;
	}
}