package com.ssafy.withview.service;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.ssafy.withview.repository.ChannelRepository;
import com.ssafy.withview.repository.WebSocketSubscribeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelValueService {

	private final ChannelTopic channelValueChannelTopic;
	private final RedisTemplate redisTemplate;
	private final WebSocketSubscribeRepository webSocketSubscribeRepository;
	private final ChannelRepository channelRepository;

	public Long enterChannel(Long userSeq, Long channelSeq, Long serverSeq) {
		return webSocketSubscribeRepository.userSubscribeChannelChat(userSeq, channelSeq, serverSeq);
	}

	public Long leaveChannel(Long userSeq, Long channelSeq) {
		return webSocketSubscribeRepository.userUnsubscribeChannelChat(userSeq, channelSeq);
	}

	public Set<Long> getChannelMemberValue(Long channelSeq) {
		return webSocketSubscribeRepository.getChannelMembers(channelSeq);
	}

	/**
	 * 채팅방에 메시지 발송
	 */
	public void sendChannelValue(String channelValueJson) {
		redisTemplate.convertAndSend(channelValueChannelTopic.getTopic(), channelValueJson);
	}
}
