package com.ssafy.withview.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatService {

	private final ChannelTopic channelChattingTopic;
	private final ChannelTopic friendsChattingTopic;
	private final RedisTemplate redisTemplate;

	/**
	 * 채팅방에 메시지 발송
	 */
	public void sendChannelChatMessage(String chatMessage) {
		redisTemplate.convertAndSend(channelChattingTopic.getTopic(), chatMessage);
	}

	public void sendFriendsChatMessage(String chatMessage) {
		redisTemplate.convertAndSend(friendsChattingTopic.getTopic(), chatMessage);
	}
}
