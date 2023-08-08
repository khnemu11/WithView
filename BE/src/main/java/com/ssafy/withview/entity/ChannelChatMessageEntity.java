package com.ssafy.withview.entity;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ssafy.withview.dto.ChannelChatDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document("channel_chat")
@NoArgsConstructor
public class ChannelChatMessageEntity {

	@Indexed
	private Long channelSeq;
	private String message;
	private Long userSeq;
	private LocalDateTime sendTime;

	public static ChannelChatDto toDto(ChannelChatMessageEntity entity) {
		return ChannelChatDto.builder()
			.channelSeq(entity.getChannelSeq())
			.userSeq(entity.getUserSeq())
			.message(entity.getMessage())
			.sendTime(entity.getSendTime())
			.build();
	}

	@Builder
	public ChannelChatMessageEntity(Long channelSeq, String message, Long userSeq, LocalDateTime sendTime) {
		this.channelSeq = channelSeq;
		this.message = message;
		this.userSeq = userSeq;
		this.sendTime = sendTime;
	}
}
