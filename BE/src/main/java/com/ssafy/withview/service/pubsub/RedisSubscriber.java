package com.ssafy.withview.service.pubsub;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.withview.dto.ChannelChatMessageDto;
import com.ssafy.withview.dto.ChannelValueDto;
import com.ssafy.withview.dto.FriendsChatMessageDto;

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
	public void sendChannelMessage(String publishMessage) {
		try {
			// ChatMessageDTO 객체로 매핑
			ChannelChatMessageDto chatMessage = objectMapper.readValue(publishMessage, ChannelChatMessageDto.class);
			log.info("{}번 채널 채팅 전송, 전송자 {}, 내용 {}", chatMessage.getChannelSeq(), chatMessage.getUserSeq(),
				chatMessage.getMessage());
			messagingTemplate.convertAndSend("/api/sub/chat/channel/" + chatMessage.getChannelSeq(),
				chatMessage);
		} catch (Exception e) {
			log.error("Exception {}", e);
		}
	}

	public void sendFriendsMessage(String publishMessage) {
		try {
			// ChatMessageDTO 객체로 매핑
			FriendsChatMessageDto chatMessage = objectMapper.readValue(publishMessage, FriendsChatMessageDto.class);
			log.info("{}번 친구 채팅 전송, 전송자 {}, 내용 {}", chatMessage.getFriendsChatSeq(), chatMessage.getSendUserSeq(),
				chatMessage.getMessage());
			messagingTemplate.convertAndSend("/api/sub/chat/friends/" + chatMessage.getFriendsChatSeq(),
				chatMessage);
		} catch (Exception e) {
			log.error("Exception {}", e);
		}
	}

	public void sendChannelValue(String publishMessage) {
		try {
			ChannelValueDto channelValue = objectMapper.readValue(publishMessage, ChannelValueDto.class);
			log.info("{}번 서버 인원 변경 전송", channelValue.getServerSeq());
			messagingTemplate.convertAndSend("/api/sub/server/" + channelValue.getServerSeq(), channelValue);
		} catch (Exception e) {
			log.error("Exception {}", e);
		}
	}

	public void sendCanvas(String publishMessage) {
		try {
			ChannelValueDto channelValue = objectMapper.readValue(publishMessage, ChannelValueDto.class);
			log.info("{}번 서버 인원 변경 전송", channelValue.getServerSeq());
			messagingTemplate.convertAndSend("/api/sub/canvas/" + channelValue.getServerSeq(), channelValue);
		} catch (Exception e) {
			log.error("Exception {}", e);
		}
	}
}
