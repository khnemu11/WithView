package com.ssafy.withview.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.ssafy.withview.dto.ChannelChatDto;
import com.ssafy.withview.dto.FriendsChatMessageDto;
import com.ssafy.withview.dto.FriendsChatRoomsSeqDto;
import com.ssafy.withview.dto.FriendsChatRoomsUserInfoDto;
import com.ssafy.withview.dto.FriendsChatRoomsUserInfoForPubSendDto;
import com.ssafy.withview.dto.UserSeqDto;
import com.ssafy.withview.service.ChannelChatService;
import com.ssafy.withview.service.ChatPublisher;
import com.ssafy.withview.service.FriendsChatRoomService;
import com.ssafy.withview.service.FriendsChatService;
import com.ssafy.withview.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {

	private final ChatPublisher chatPublisher;
	private final ChannelChatService channelChatService;
	private final FriendsChatService friendsChatService;
	private final FriendsChatRoomService friendsChatRoomService;
	private final UserService userService;

	// websocket "/api/pub/chat/channel/message"로 들어오는 메시징을 처리한다.
	@MessageMapping("/chat/channel/message")
	public void channelChatMessage(ChannelChatDto message) {
		message.setSendTime(LocalDateTime.now());
		channelChatService.insertChannelChat(message);
		chatPublisher.sendChannelChatMessage(message.toJson());
	}

	// websocket "/api/pub/chat/friends/message"로 들어오는 메시징을 처리한다.
	@MessageMapping("/chat/friends/message")
	public void friendsChatMessage(FriendsChatMessageDto message) {
		message.setMessageSeq(friendsChatService.getFriendsChatRoomLastMessageSeq(message.getFriendsChatRoomSeq()));
		message.setSendTime(LocalDateTime.now());
		friendsChatService.insertFriendsChat(message);
		chatPublisher.sendFriendsChatMessage(message.toJson());
	}

	@MessageMapping("/chat/friends/chatroominfo")
	public void friendsChatRoomInfo(UserSeqDto userSeqDto) {
		Long userSeq = userSeqDto.getUserSeq();
		List<FriendsChatRoomsSeqDto> friendsChatRoomsByPartnerSeq = friendsChatRoomService.findFriendsChatRoomsByPartnerSeq(
			userSeq);

		List<FriendsChatRoomsUserInfoDto> friendsChatRoomsUserInfoDtos = friendsChatRoomsByPartnerSeq.stream()
			.map(dto -> {
				FriendsChatMessageDto lastFriendsChatMessage = friendsChatService.getLastFriendsChatMessage(
					dto.getChatRoomSeq());

				Long lastFriendsChatMessageSeq = lastFriendsChatMessage.getMessageSeq();
				Long unreadCount = friendsChatService.getUnreadFriendsChatMessageCount(
					dto.getChatRoomSeq(), userSeq,
					lastFriendsChatMessageSeq);

				return FriendsChatRoomsUserInfoDto.builder()
					.chatRoomSeq(dto.getChatRoomSeq())
					.userDto(userService.getProfile(dto.getPartnerSeq()))
					.friendsChatMessageDto(lastFriendsChatMessage)
					.unread(unreadCount)
					.build();
			})
			.collect(Collectors.toList());

		friendsChatRoomsUserInfoDtos.sort((a, b) -> {
			LocalDateTime sendTimeA = a.getFriendsChatMessageDto().getSendTime();
			LocalDateTime sendTimeB = b.getFriendsChatMessageDto().getSendTime();
			return sendTimeB.compareTo(sendTimeA);
		});

		FriendsChatRoomsUserInfoForPubSendDto pubSendDto = FriendsChatRoomsUserInfoForPubSendDto.builder()
			.userSeq(userSeq)
			.friendsChatRoomsUserInfoDtos(friendsChatRoomsUserInfoDtos)
			.build();
		chatPublisher.sendFriendsChatRoomInfo(pubSendDto.toJson());
	}

	@MessageMapping("/chat/friends/{friendsChatRoomSeq}")
	public void setRecentChatMessageSeq(@DestinationVariable Long friendsChatRoomSeq) {
		Long lastMessageSeq = friendsChatService.setFriendsChatRoomLastMessageSeq(friendsChatRoomSeq);
		log.info("{}번 1대1 채팅방 구독 시작, 마지막 메시지 seq: {}", friendsChatRoomSeq, lastMessageSeq);
	}

	@GetMapping("/chat/view")
	public String chatView(ChannelChatDto message) {
		return "chat";
	}
}
