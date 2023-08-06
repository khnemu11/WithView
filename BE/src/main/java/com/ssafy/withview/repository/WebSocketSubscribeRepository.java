package com.ssafy.withview.repository;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class WebSocketSubscribeRepository {

	/**
	 * 어떤 유저가 어떤 채널에 참여해있는지 기억하는 값 : HashOperations<String, Long, ChatRoom> hashOpsUserChatRoom
	 * 어떤 채널에 어떤 유저가 있는지 기억하는 값 : SetOperations<ChatRoom, Long> setOpsChatRoom
	 */

	private static final String ENTER_CHAT_CHANNEL = "ENTER_CHAT_CHANNEL";

	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> hashOpsUserEnterChatChannelInfo;
	@Resource(name = "redisTemplate")
	private SetOperations<String, String> setOpsChatRoomMemberValue;
	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valOpsRoomUserValue;

	public Long userSubscribeChatRoom(Long userSeq, Long channelSeq) {
		hashOpsUserEnterChatChannelInfo.put(ENTER_CHAT_CHANNEL, String.valueOf(userSeq), String.valueOf(channelSeq));
		setOpsChatRoomMemberValue.add(String.valueOf(channelSeq), String.valueOf(userSeq));
		return channelSeq;
	}

	public Long userUnsubscribeChatRoom(Long userSeq, Long channelSeq) {
		hashOpsUserEnterChatChannelInfo.delete(ENTER_CHAT_CHANNEL, String.valueOf(userSeq));
		setOpsChatRoomMemberValue.remove(String.valueOf(channelSeq), String.valueOf(userSeq));
		return channelSeq;
	}

	public Long getUserChatRoom(Long userSeq) {
		return Long.parseLong(hashOpsUserEnterChatChannelInfo.get(ENTER_CHAT_CHANNEL, String.valueOf(userSeq)));
	}

	public Set<Long> getChatRoomMembers(Long channelSeq) {
		return setOpsChatRoomMemberValue.members(String.valueOf(channelSeq)).stream()
			.map(Long::parseLong)
			.collect(Collectors.toSet());
	}
}
