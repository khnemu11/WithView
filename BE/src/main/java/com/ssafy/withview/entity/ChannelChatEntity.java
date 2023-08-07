package com.ssafy.withview.entity;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ssafy.withview.dto.ChannelChatMessageDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document("channel_chat")
@NoArgsConstructor
public class ChannelChatEntity {

	@Indexed
	private Long channelSeq;
	private String message;
	private Long userSeq;
	private LocalDateTime sendTime;

	public static ChannelChatMessageDto toDto(ChannelChatEntity entity) {
		return ChannelChatMessageDto.builder()
			.channelSeq(entity.getChannelSeq())
			.userSeq(entity.getUserSeq())
			.message(entity.getMessage())
			.sendTime(entity.getSendTime())
			.build();
	}
	
	@Builder
	public ChannelChatEntity(Long channelSeq, String message, Long userSeq, LocalDateTime sendTime) {
		this.channelSeq = channelSeq;
		this.message = message;
		this.userSeq = userSeq;
		this.sendTime = sendTime;
	}
}
