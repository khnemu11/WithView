package com.ssafy.withview.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ssafy.withview.entity.ChannelChatMessageEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChannelChatDto implements Serializable {

	private static final long serialVersionUID = 64946284924512L;

	private String message;
	private Long channelSeq;
	private Long userSeq;
	private LocalDateTime sendTime;

	public static ChannelChatMessageEntity toEntity(ChannelChatDto dto) {
		return ChannelChatMessageEntity.builder()
			.channelSeq(dto.getChannelSeq())
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
