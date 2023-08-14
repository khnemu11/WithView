package com.ssafy.withview.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssafy.withview.dto.ChannelChatDto;
import com.ssafy.withview.dto.ChannelChatSendDto;
import com.ssafy.withview.dto.FriendsChatMessageDto;
import com.ssafy.withview.dto.FriendsChatRoomsSeqDto;
import com.ssafy.withview.dto.FriendsChatRoomsUserInfoDto;
import com.ssafy.withview.dto.FriendsChatRoomsUserInfoForPubSendDto;
import com.ssafy.withview.dto.UserDto;
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
	public void channelChatMessage(ChannelChatDto channelChatDto) {
		channelChatDto.setSendTime(LocalDateTime.now());
		UserDto profile = userService.getProfile(channelChatDto.getUserSeq());
		ChannelChatSendDto channelChatSendDto = ChannelChatSendDto.builder()
			.message(channelChatDto.getMessage())
			.channelSeq(channelChatDto.getChannelSeq())
			.sendTime(channelChatDto.getSendTime())
			.userDto(profile)
			.build();
		channelChatService.insertChannelChat(channelChatDto);
		chatPublisher.sendChannelChatMessage(channelChatSendDto.toJson());
	}

	// websocket "/api/pub/chat/friends/message"로 들어오는 메시징을 처리한다.
	@MessageMapping("/chat/friends/message")
	public void friendsChatMessage(FriendsChatMessageDto message) {
		Long friendsChatRoomLastMessageSeq = friendsChatService.getFriendsChatRoomLastMessageSeq(
			message.getFriendsChatRoomSeq());
		message.setMessageSeq(friendsChatRoomLastMessageSeq);
		message.setSendTime(LocalDateTime.now());
		friendsChatService.insertFriendsChat(message);
		friendsChatService.setFriendsChatRoomLastMessageSeqJpa(message.getFriendsChatRoomSeq(), message.getFromUserSeq(), friendsChatRoomLastMessageSeq);
		chatPublisher.sendFriendsChatMessage(message.toJson());
	}

	@MessageMapping("/chat/friends/chatroominfo")
	public void friendsChatRoomInfo(UserSeqDto userSeqDto) {
		Long userSeq = userSeqDto.getUserSeq();
		log.info("{}번 유저의 채팅 목록 불러오기", userSeq);
		List<FriendsChatRoomsSeqDto> friendsChatRoomsByPartnerSeq = friendsChatRoomService.findFriendsChatRoomsByPartnerSeq(
			userSeq);

		if (friendsChatRoomsByPartnerSeq.isEmpty()) {
			chatPublisher.sendFriendsChatRoomInfo(FriendsChatRoomsUserInfoForPubSendDto.builder()
				.friendsChatRoomsUserInfoDtos(new ArrayList<>())
				.userSeq(userSeq)
				.build().toJson());
		}

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
		Long lastMessageSeq = friendsChatService.setFriendsChatRoomLastMessageSeqRedis(friendsChatRoomSeq);
		log.info("{}번 1대1 채팅방 구독 시작, 마지막 메시지 seq: {}", friendsChatRoomSeq, lastMessageSeq);
	}

	@MessageMapping("chat/friends/{friendsChatRoomSeq}/{userSeq}")
	public void setUnreadMessageSeq(@DestinationVariable Long friendsChatRoomSeq, @DestinationVariable Long userSeq) {
		FriendsChatMessageDto lastFriendsChatMessage = friendsChatService.getLastFriendsChatMessage(friendsChatRoomSeq);
		Long lastReadMessageSeq = lastFriendsChatMessage.getMessageSeq();
		friendsChatService.setFriendsChatRoomLastMessageSeqJpa(friendsChatRoomSeq, userSeq, lastReadMessageSeq);
	}

	@GetMapping("/chat/test")
	@ResponseBody
	public List<FriendsChatMessageDto> test(@RequestParam Long test) {
		return friendsChatService.getFriendsChatMessagesByPage(test, 1);
	}
}
