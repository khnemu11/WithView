package com.ssafy.withview.controller;

import java.time.LocalDate;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.ssafy.withview.dto.ChatMessageDto;
import com.ssafy.withview.service.ChatService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ChatController {

	private final ChatService chatService;

	// websocket "/api/pub/chat/message"로 들어오는 메시징을 처리한다.
	@MessageMapping("/chat/message/channel")
	public void chatMessage(ChatMessageDto message) {
		message.setSendTime(LocalDate.now());
		// Websocket 에 발행된 메시지를 redis로 발행 (publish)
		chatService.sendChatMessage(message);
	}

	// @MessageMapping("/chat/friend")
	// public void chatFriend(ChatMessageDto message, Long friendChatSeq) {
	// 	message.getChatRoom().setRoomType(RoomType.FRIEND);
	// }
}
