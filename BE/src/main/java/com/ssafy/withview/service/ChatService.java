package com.ssafy.withview.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.ssafy.withview.dto.ChatMessageDto;
import com.ssafy.withview.repository.ChannelChatRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatService {

	private final ChannelTopic channelTopic;
	private final RedisTemplate redisTemplate;
	private final ChannelChatRepository channelChatRepository;

	/**
	 * destination 정보에서 roomId 추출
	 */
	public String getServerSeq(String destination) {
		int lastIndex = destination.lastIndexOf('/');
		if (lastIndex != -1) {
			return destination.substring(lastIndex + 1);
		} else {
			return "";
		}
	}

	/**
	 * 채팅방에 메시지 발송
	 */
	public void sendChatMessage(ChatMessageDto chatMessage) {
		// chatMessage.setUserCount(channelChatRepository.getUserCount(chatMessage.getUserSeq()));

		redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
	}
}
