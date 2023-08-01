package com.ssafy.withview.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.ssafy.withview.repository.ChatRoomRepository;
import com.ssafy.withview.repository.dto.ChatMessageDTO;
import com.ssafy.withview.service.pubsub.RedisPublisher;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ChatController {

	private final RedisPublisher redisPublisher;
	private final ChatRoomRepository chatRoomRepository;

	// websocket "/api/pub/chat/message"로 들어오는 메시징을 처리한다.
	@MessageMapping("/chat/message")
	public void message(ChatMessageDTO message) {
		if (ChatMessageDTO.MessageType.ENTER.equals(message.getType())) {
			chatRoomRepository.enterChatRoom(message.getServerSeq());
			message.setMessage(message.getSender());
		}
		redisPublisher.publish(chatRoomRepository.getTopic(message.getServerSeq()), message);
	}
}
