package com.ssafy.withview.config.handler;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.ssafy.withview.dto.ChatRoom;
import com.ssafy.withview.repository.ChannelChatRepository;
import com.ssafy.withview.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

	private final ChannelChatRepository channelChatRepository;
	private final ChatService chatService;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		if (StompCommand.CONNECT == accessor.getCommand()) {
			Long userSeq = Long.valueOf(Optional.ofNullable(accessor.getFirstNativeHeader("userSeq")).orElse("0L"));
			String id = Optional.ofNullable(accessor.getFirstNativeHeader("id")).orElse("");
			if (userSeq == 0L || id.equals("")) {
				log.warn("Websocket connection error");
				return message;
			}
			channelChatRepository.userConnectSocket(id, userSeq);
		} else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청

			Long userSeq = Long.valueOf(Optional.ofNullable(accessor.getFirstNativeHeader("userSeq")).orElse("0L"));
			// header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
			String username = extractUsername(message.toString());
			String serverSeq = chatService.getServerSeq(
				Optional.ofNullable((String)message.getHeaders().get("channelSeq")).orElse("InvalidRoomId"));

			// 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
			String sessionId = (String)message.getHeaders().get("simpSessionId");
			// channelChatRepository.setUserEnterInfo(sessionId, serverSeq);

			// 채팅방의 인원수를 +1한다.
			// channelChatRepository.plusUserCount(serverSeq);

			// 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
			// String name = Optional.ofNullable((Principal)message.getHeaders().get("simpUser"))
			// 	.map(Principal::getName)
			// 	.orElse("UnknownUser");
			//
			// chatService.sendChatMessage(ChatMessageDto.builder()
			// 	.type(ChatMessageDto.MessageType.ENTER)
			// 	.channelSeq(serverSeq)
			// 	.userSeq(name)
			// 	.build());

			// log.info("SUBSCRIBED {}, {}", name, serverSeq);

		} else if (StompCommand.DISCONNECT == accessor.getCommand()
			|| StompCommand.UNSUBSCRIBE == accessor.getCommand()) { // Websocket 연결 종료

			// 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
			String sessionId = (String)message.getHeaders().get("simpSessionId");
			ChatRoom roomId = channelChatRepository.getUserEnterRoomValue(sessionId);

			// 채팅방의 인원수를 -1한다.
			// channelChatRepository.minusUserCount(roomId);

			// 클라이언트 퇴장 메시지를 채팅방에 발송한다.(redis publish)
			// String name = Optional.ofNullable((Principal)message.getHeaders().get("simpUser"))
			// 	.map(Principal::getName)
			// 	.orElse("UnknownUser");
			//
			// chatService.sendChatMessage(
			// 	ChatMessageDto.builder()
			// 		.type(ChatMessageDto.MessageType.LEAVE)
			// 		.channelSeq(roomId)
			// 		.userSeq(name)
			// 		.build());

			// 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
			channelChatRepository.removeUserEnterInfo(sessionId);
			log.info("DISCONNECTED {}, {}", sessionId, roomId);
		}
		return message;
	}

	public static String extractUsername(String inputString) {
		String regexPattern = "Username: (.*?);";
		Pattern pattern = Pattern.compile(regexPattern);
		Matcher matcher = pattern.matcher(inputString);

		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null; // or throw an exception to indicate username not found.
		}
	}
}