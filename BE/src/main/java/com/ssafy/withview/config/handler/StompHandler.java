package com.ssafy.withview.config.handler;

import java.util.Optional;

import com.ssafy.withview.repository.FriendRepository;
import com.ssafy.withview.repository.ServerRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.ssafy.withview.constant.RoomType;
import com.ssafy.withview.dto.ChatRoomDto;
import com.ssafy.withview.repository.WebSocketRepository;
import com.ssafy.withview.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

	private final WebSocketRepository webSocketRepository;
	private final FriendRepository friendRepository;
	private final ServerRepository serverRepository;
	private final ChatService chatService;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		if (StompCommand.CONNECT == accessor.getCommand()) {
			Long userSeq = Long.valueOf(Optional.ofNullable(accessor.getFirstNativeHeader("userseq")).orElse("0L"));
			Long serverSeq = Long.valueOf(Optional.ofNullable(accessor.getFirstNativeHeader("serverseq")).orElse("0L"));
			// Long userSeq = Long.valueOf(Optional.ofNullable(accessor.getFirstNativeHeader("userseq")).orElse("0L"));
			// String id = Optional.ofNullable(accessor.getFirstNativeHeader("id")).orElse("");
			// String serverSeq = Optional.ofNullable(accessor.getFirstNativeHeader("serverseq")).orElse("");
			// if (userSeq == 0L || id.equals("")) {
			// 	log.warn("Websocket connection error");
			// 	return message;
			// }
			// channelChatRepository.userConnectSocket(id, userSeq);
		} else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
			// header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
			Long userSeq = Long.valueOf(Optional.ofNullable(accessor.getFirstNativeHeader("userseq")).orElse("0L"));
			Long serverSeq = Long.valueOf(Optional.ofNullable(accessor.getFirstNativeHeader("serverseq")).orElse("0L"));
			Long channelSeq = Long.valueOf(
				Optional.ofNullable(accessor.getFirstNativeHeader("channelseq")).orElse("0L"));
			RoomType roomType = RoomType.valueOf(
				(Optional.ofNullable((String)accessor.getFirstNativeHeader("roomtype")).orElse("INVALID")));

			ChatRoomDto chatRoom = ChatRoomDto.builder()
				.roomType(roomType)
				.seq(channelSeq)
				.build();

			webSocketRepository.userSubscribeChatRoom(userSeq, chatRoom);

			// channelChatRepository.getChannelValueOfServer(serverSeq);
			//
			// chatService.sendChannelValue();

		} else if (StompCommand.DISCONNECT == accessor.getCommand()
			|| StompCommand.UNSUBSCRIBE == accessor.getCommand()) { // Websocket 연결 종료

			// 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
			// String sessionId = (String)message.getHeaders().get("simpSessionId");
			// ChatRoom roomId = channelChatRepository.getUserEnterRoomValue(sessionId);

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
			// channelChatRepository.removeUserEnterInfo(sessionId);
			// log.info("DISCONNECTED {}, {}", sessionId, roomId);
		}
		return message;
	}
}