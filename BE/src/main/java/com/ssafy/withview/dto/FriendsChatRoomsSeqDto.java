package com.ssafy.withview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FriendsChatRoomsSeqDto {

	private Long userSeq;
	private Long chatRoomSeq;
	private Long partnerSeq;

	public FriendsChatRoomsSeqDto(Long userSeq) {
		this.userSeq = userSeq;
	}
}
