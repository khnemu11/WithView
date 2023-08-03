package com.ssafy.withview.dto;

import java.time.LocalDate;

import com.ssafy.withview.constant.MessageType;
import com.ssafy.withview.constant.RoomType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatMessageDto {

	private MessageType type;
	private String message;
	private RoomType roomType;
	private Long seq;
	private Long sender; // 보낸 사람 userSeq
	private LocalDate sendTime;
}
