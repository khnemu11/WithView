package com.ssafy.withview.entity;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ssafy.withview.dto.FriendsChatMessageDto;

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
	private Long messageSeq;
	private Long fromUserSeq;
	private Long toUserSeq;
	private LocalDateTime sendTime;

	public static FriendsChatMessageDto toDto(FriendsChatMessageEntity entity) {
		return FriendsChatMessageDto.builder()
			.friendsChatRoomSeq(entity.getFriendsChatRoomSeq())
			.message(entity.getMessage())
			.messageSeq(entity.getMessageSeq())
			.fromUserSeq(entity.getFromUserSeq())
			.toUserSeq(entity.getToUserSeq())
			.sendTime(entity.getSendTime())
			.build();
	}

	@Builder
	public FriendsChatMessageEntity(Long friendsChatRoomSeq, String message, Long messageSeq, Long fromUserSeq,
		Long toUserSeq, LocalDateTime sendTime) {
		this.friendsChatRoomSeq = friendsChatRoomSeq;
		this.message = message;
		this.messageSeq = messageSeq;
		this.fromUserSeq = fromUserSeq;
		this.toUserSeq = toUserSeq;
		this.sendTime = sendTime;
	}
}
