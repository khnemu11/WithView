package com.ssafy.withview.service;

import com.ssafy.withview.dto.FriendsChatMessageDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.ssafy.withview.dto.ChannelChatMessageDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatService {

	private final ChannelTopic channelChattingTopic;
	private final ChannelTopic friendsChattingTopic;
	private final RedisTemplate redisTemplate;

	/**
	 * 채팅방에 메시지 발송
	 */
	public void sendChannelChatMessage(ChannelChatMessageDto chatMessage) {
		redisTemplate.convertAndSend(channelChattingTopic.getTopic(), chatMessage);
	}

	public void sendFriendsChatMessage(FriendsChatMessageDto chatMessage) {
		redisTemplate.convertAndSend(friendsChattingTopic.getTopic(), chatMessage);
	}
}
