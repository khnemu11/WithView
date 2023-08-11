package com.ssafy.withview.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FriendsChatRoomsUserInfoForPubSendDto implements Serializable {

	private static final long serialVersionUID = 81822976203816232L;

	private Long userSeq;
	private List<FriendsChatRoomsUserInfoDto> friendsChatRoomsUserInfoDtos;

	public String toJson() {
		String json = null;
		try {
			json = new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return json;
	}
}
