package com.ssafy.withview.service.pubsub;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.ssafy.withview.repository.dto.ChatMessageDTO;

import lombok.RequiredArgsConstructor;

@SuppressWarnings("CheckStyle")
@RequiredArgsConstructor
@Service
public class RedisPublisher {
	private final RedisTemplate<String, Object> redisTemplate;

	public void publish(ChannelTopic topic, ChatMessageDTO message) {
		redisTemplate.convertAndSend(topic.getTopic(), message);
	}
}
