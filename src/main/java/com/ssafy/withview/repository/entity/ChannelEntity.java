package com.ssafy.withview.repository.entity;

import javax.persistence.*;

import com.ssafy.withview.repository.dto.ChannelDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Entity
@ToString
@Table(name = "channel")
public class ChannelEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;
	private String name;
	@Builder.Default
	private int limitPeople=5;
	private int serverSeq;

	public static ChannelDto toDto(ChannelEntity entity){
		return ChannelDto.builder()
			.name(entity.getName())
			.limitPeople(entity.getLimitPeople())
			.serverSeq(entity.getServerSeq()).build();
	}

	@Builder
	public ChannelEntity(String name, int limitPeople, int serverSeq) {
		this.name = name;
		this.limitPeople = limitPeople;
		this.serverSeq = serverSeq;
	}
}