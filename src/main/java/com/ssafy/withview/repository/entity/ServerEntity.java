package com.ssafy.withview.repository.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.ssafy.withview.repository.dto.ServerDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Builder
public class ServerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;
	private String name;
	private int limitChannel=5;
	private int hostSeq;
	public static ServerDto toDto(ServerEntity serverEntity){
		return ServerDto.builder()
			.seq(serverEntity.getSeq())
			.name(serverEntity.getName())
			.limitChannel(serverEntity.getLimitChannel())
			.hostSeq(serverEntity.getHostSeq()).build();
	}
}