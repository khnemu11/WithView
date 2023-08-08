package com.ssafy.withview.controller;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.ssafy.withview.dto.ChannelChatDto;
import com.ssafy.withview.dto.FriendsChatDto;
import com.ssafy.withview.repository.WebSocketSubscribeRepository;
import com.ssafy.withview.service.ChannelChatService;
import com.ssafy.withview.service.ChatPublisher;
import com.ssafy.withview.service.FriendsChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {

	private final ChatPublisher chatPublisher;
	private final ChannelChatService channelChatService;
	private final FriendsChatService friendsChatService;
	private final WebSocketSubscribeRepository webSocketSubscribeRepository;

	// websocket "/api/pub/chat/channel/message"로 들어오는 메시징을 처리한다.
	@MessageMapping("/chat/channel/message")
	public void channelChatMessage(ChannelChatDto message) {
		message.setSendTime(LocalDateTime.now());
		channelChatService.insertChannelChat(message);
		chatPublisher.sendChannelChatMessage(message.toJson());
	}

	// websocket "/api/pub/chat/friends/message"로 들어오는 메시징을 처리한다.
	@MessageMapping("/chat/friends/message")
	public void friendsChatMessage(FriendsChatDto message) {
		message.setSendTime(LocalDateTime.now());
		webSocketSubscribeRepository.addFriendsChatUnreadCount(message.getFriendsChatRoomSeq(), message.getToUserSeq());
		friendsChatService.insertFriendsChat(message);
		chatPublisher.sendFriendsChatMessage(message.toJson());
	}

	@GetMapping("/chat/view")
	public String chatView(ChannelChatDto message) {
		return "chat";
	}
}
