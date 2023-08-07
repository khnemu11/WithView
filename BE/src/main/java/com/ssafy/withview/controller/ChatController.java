package com.ssafy.withview.controller;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.ssafy.withview.dto.ChannelChatMessageDto;
import com.ssafy.withview.dto.FriendsChatMessageDto;
import com.ssafy.withview.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {

	private final ChatService chatService;

	// websocket "/api/pub/chat/channel/message"로 들어오는 메시징을 처리한다.
	@MessageMapping("/chat/channel/message")
	public void channelChatMessage(ChannelChatMessageDto message) {
		message.setSendTime(LocalDateTime.now());
		chatService.sendChannelChatMessage(message.toJson());
	}

	// websocket "/api/pub/chat/friends/message"로 들어오는 메시징을 처리한다.
	@MessageMapping("/chat/friends/message")
	public void friendsChatMessage(FriendsChatMessageDto message) {
		message.setSendTime(LocalDateTime.now());
		chatService.sendFriendsChatMessage(message.toJson());
	}

	@GetMapping("/chat/view")
	public String chatView(ChannelChatMessageDto message) {
		return "chat";
	}
}
