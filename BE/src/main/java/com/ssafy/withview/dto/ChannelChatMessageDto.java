package com.ssafy.withview.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChannelChatMessageDto {

	private String message;
	private Long channelSeq;
	private Long userSeq;
	private LocalDateTime sendTime;
}
