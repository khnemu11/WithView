package com.ssafy.withview.dto;

import com.ssafy.withview.constant.RoomType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatRoomDto {

	private RoomType roomType;
	private Long seq;
}
