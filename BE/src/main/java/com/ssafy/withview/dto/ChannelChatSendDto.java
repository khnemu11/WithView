package com.ssafy.withview.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChannelChatSendDto {

	private Long channelSeq;
	private String message;
	private UserDto userDto;
	private LocalDateTime sendTime;

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
