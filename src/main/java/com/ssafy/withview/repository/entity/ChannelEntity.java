package com.ssafy.withview.repository.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.ssafy.withview.repository.dto.ChannelDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@ToString
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
			.seq(entity.getSeq())
			.name(entity.getName())
			.limitPeople(entity.getLimitPeople())
			.serverSeq(entity.getServerSeq()).build();
	}
}