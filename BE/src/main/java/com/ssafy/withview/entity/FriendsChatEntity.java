package com.ssafy.withview.entity;

import com.ssafy.withview.dto.ChannelChatDto;
import com.ssafy.withview.dto.FriendsChatDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document("friends_chat")
@NoArgsConstructor
public class FriendsChatEntity {

	@Indexed
	private Long friendsChatSeq;
	private String message;
	private Long userSeq;
	private LocalDateTime sendTime;

	public static FriendsChatDto toDto(FriendsChatEntity entity) {
		return FriendsChatDto.builder()
			.friendsChatSeq(entity.getFriendsChatSeq())
			.userSeq(entity.getUserSeq())
			.message(entity.getMessage())
			.sendTime(entity.getSendTime())
			.build();
	}

	@Builder
	public FriendsChatEntity(Long friendsChatSeq, String message, Long userSeq, LocalDateTime sendTime) {
		this.friendsChatSeq = friendsChatSeq;
		this.message = message;
		this.userSeq = userSeq;
		this.sendTime = sendTime;
	}
}
