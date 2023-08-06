package com.ssafy.withview.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class FriendsChatMessageDto {

	private String message;
	private Long friendsChatSeq;
	private Long sendUserSeq;
	private LocalDateTime sendTime;
}
