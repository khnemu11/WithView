package com.ssafy.withview.controller;

import com.ssafy.withview.service.ChatService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.ssafy.withview.repository.ChatRoomRepository;
import com.ssafy.withview.repository.dto.ChatMessageDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ChatController {

	private final ChatRoomRepository chatRoomRepository;
	private final ChatService chatService;

	// websocket "/api/pub/chat/message"로 들어오는 메시징을 처리한다.
	@MessageMapping("/chat/message")
	public void message(ChatMessageDTO message) {

		message.setUserCount(chatRoomRepository.getUserCount(message.getServerSeq()));
		// Websocket 에 발행된 메시지를 redis로 발행 (publish)
		chatService.sendChatMessage(message);
	}
}
