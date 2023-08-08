package com.ssafy.withview.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FriendsChatRoomsUserInfoDto {

	private Long chatRoomSeq;
	private UserDto userDto;
}
