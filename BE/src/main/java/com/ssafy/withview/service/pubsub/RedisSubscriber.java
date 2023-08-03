package com.ssafy.withview.service.pubsub;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.withview.dto.ChannelValueDto;
import com.ssafy.withview.dto.ChatMessageDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

	private final ObjectMapper objectMapper;
	private final SimpMessageSendingOperations messagingTemplate;

	/**
	 * Redis에서 메시지가 발행(publish)되면 대기하고 있던 Redis Subscriber가 해당 메시지를 받아 처리한다.
	 * ChatMessageDto로 형태를 바꿔 프론트엔드로 전달
	 */
	public void sendMessage(String publishMessage) {
		try {
			// ChatMessageDTO 객체로 매핑
			ChatMessageDto chatMessage = objectMapper.readValue(publishMessage, ChatMessageDto.class);
			// 채팅방을 구독한 클라이언트에게 메시지 발송
			messagingTemplate.convertAndSend(
				"/api/sub/chat/" + chatMessage.getRoomType() + "/" + chatMessage.getSeq(),
				chatMessage);
		} catch (Exception e) {
			log.error("Exception {}", e);
		}
	}

	public void sendChannelValue(String publishMessage) {
		try {
			// ChatMessageDTO 객체로 매핑
			ChannelValueDto chatMessage = objectMapper.readValue(publishMessage, ChannelValueDto.class);
			// 채팅방을 구독한 클라이언트에게 메시지 발송
			messagingTemplate.convertAndSend(
				"/api/sub/channelvalue/" + chatMessage.getServerSeq(),
				chatMessage);
		} catch (Exception e) {
			log.error("Exception {}", e);
		}
	}
}
