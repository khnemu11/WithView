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
public class WebSocketSubscribeService {

	private final ChannelTopic channelValueChannelTopic;
	private final RedisTemplate redisTemplate;
	private final WebSocketSubscribeRepository webSocketSubscribeRepository;
	private final ChannelRepository channelRepository;

	public Long enterChannel(Long userSeq, Long channelSeq) {
		return webSocketSubscribeRepository.userSubscribeChatRoom(userSeq, channelSeq);
	}

	public Long leaveChannel(Long userSeq, Long channelSeq) {
		return webSocketSubscribeRepository.userUnsubscribeChatRoom(userSeq, channelSeq);
	}

	public Set<Long> getChannelMemberValue(Long channelSeq) {
		// Set<Long> chatRoomMembers = webSocketSubscribeRepository.getChatRoomMembers(channelSeq);
		// HashMap<Long, Set<Long>> map = new HashMap<>();
		// map.put(channelSeq, chatRoomMembers);
		return webSocketSubscribeRepository.getChatRoomMembers(channelSeq);
		// return map;
	}

	/**
	 * 채팅방에 메시지 발송
	 */
	public void sendChannelValue(String channelValueJson) {
		System.out.println("안녕~");
		redisTemplate.convertAndSend(channelValueChannelTopic.getTopic(), channelValueJson);
	}
}
