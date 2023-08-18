package com.ssafy.withview.dto;

import com.ssafy.withview.entity.FriendsChatRoomEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FriendsChatRoomLastReadDto {

	private Long userSeq;
	private Long lastReadMessageSeq;
}
