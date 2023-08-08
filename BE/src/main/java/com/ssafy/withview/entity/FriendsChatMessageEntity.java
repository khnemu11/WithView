package com.ssafy.withview.entity;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ssafy.withview.dto.FriendsChatDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document("friends_chat")
@NoArgsConstructor
public class FriendsChatMessageEntity {

	@Indexed
	private Long friendsChatRoomSeq;
	private String message;
	private Long userSeq;
	private LocalDateTime sendTime;

	public static FriendsChatDto toDto(FriendsChatMessageEntity entity) {
		return FriendsChatDto.builder()
			.friendsChatRoomSeq(entity.getFriendsChatRoomSeq())
			.userSeq(entity.getUserSeq())
			.message(entity.getMessage())
			.sendTime(entity.getSendTime())
			.build();
	}

	@Builder
	public FriendsChatMessageEntity(Long friendsChatRoomSeq, String message, Long userSeq, LocalDateTime sendTime) {
		this.friendsChatRoomSeq = friendsChatRoomSeq;
		this.message = message;
		this.userSeq = userSeq;
		this.sendTime = sendTime;
	}
}
