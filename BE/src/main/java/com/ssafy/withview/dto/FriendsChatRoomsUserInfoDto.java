package com.ssafy.withview.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendsChatRoomsUserInfoDto {

	private Long chatRoomSeq;
	private UserDto userDto;
	private FriendsChatMessageDto friendsChatMessageDto;
	private Long unread;

}
