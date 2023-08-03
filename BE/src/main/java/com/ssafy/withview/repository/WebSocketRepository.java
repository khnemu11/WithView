package com.ssafy.withview.repository;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.dto.ChatRoomDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class WebSocketRepository {

	/**
	 * 어떤 유저가 어떤 채널에 참여해있는지 기억하는 값 : HashOperations<String, Long, ChatRoom> hashOpsUserChatRoom
	 * 어떤 채널에 어떤 유저가 있는지 기억하는 값 : SetOperations<ChatRoom, Long> setOpsChatRoom
	 */

	private static final String CHAT_ROOM_USER = "CHAT_ROOM_USER";

	@Resource(name = "redisTemplate")
	private HashOperations<String, Long, ChatRoomDto> hashOpsUserChatRoom;
	@Resource(name = "redisTemplate")
	private SetOperations<ChatRoomDto, Long> setOpsChatRoom;

	// @Resource(name = "redisTemplate")
	// private

	public void userSubscribeChatRoom(Long userSeq, ChatRoomDto chatRoomDto) {
		hashOpsUserChatRoom.put(CHAT_ROOM_USER, userSeq, chatRoomDto);
		setOpsChatRoom.add(chatRoomDto, userSeq);
	}

	public void userUnsubscribeChatRoom(Long userSeq) {
		setOpsChatRoom.remove(getUserChatRoom(userSeq), userSeq);
		hashOpsUserChatRoom.delete(CHAT_ROOM_USER, userSeq);
	}

	public ChatRoomDto getUserChatRoom(Long userSeq) {
		return hashOpsUserChatRoom.get(CHAT_ROOM_USER, userSeq);
	}

	public Integer getChatRoomMemberCount(ChatRoomDto chatRoomDto) {
		return setOpsChatRoom.members(chatRoomDto).size();
	}

	// public ChannelValueDto getServerChannelValue(Long serverSeq) {
	//
	// }
}
