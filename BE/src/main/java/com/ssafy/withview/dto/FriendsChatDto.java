package com.ssafy.withview.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ssafy.withview.entity.FriendsChatMessageEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FriendsChatDto implements Serializable {

	private static final long serialVersionUID = 64946611254512L;

	private Long friendsChatRoomSeq;
	private Long userSeq;
	private String message;
	private LocalDateTime sendTime;

	public static FriendsChatMessageEntity toEntity(FriendsChatDto dto) {
		return FriendsChatMessageEntity.builder()
			.friendsChatRoomSeq(dto.getFriendsChatRoomSeq())
			.userSeq(dto.getUserSeq())
			.message(dto.getMessage())
			.sendTime(dto.getSendTime())
			.build();
	}

	public String toJson() {
		String json = null;
		try {
			json = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return json;
	}
}
