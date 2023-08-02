package com.ssafy.withview.dto;

import com.ssafy.withview.constant.RoomType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoom {

	private RoomType roomType;
	private Long seq;
	private Long userCount;

	public static ChatRoom create(RoomType roomType, Long seq) {
		ChatRoom chatRoom = new ChatRoom();
		chatRoom.roomType = roomType;
		chatRoom.seq = seq;
		return chatRoom;
	}
}
