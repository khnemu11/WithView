package com.ssafy.withview.repository;

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
	private HashOperations<String, Long, Long> hashOpsUserEnterChatChannelInfo;
	@Resource(name = "redisTemplate")
	private SetOperations<String, Long> setOpsChatRoomUserValue;
	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valOpsRoomUserValue;

	public Long userSubscribeChatRoom(Long userSeq, Long channelSeq) {
		// hashOpsUserEnterChatChannelInfo.put(ENTER_CHAT_CHANNEL, userSeq, channelSeq);
		// setOpsChatRoomUserValue.add(String.valueOf(channelSeq), userSeq);
		hashOpsUserEnterChatChannelInfo.put(ENTER_CHAT_CHANNEL, userSeq, channelSeq);
		valOpsRoomUserValue.set(String.valueOf(channelSeq), String.valueOf(userSeq));
		return channelSeq;
	}

	public Long userUnsubscribeChatRoom(Long userSeq, Long channelSeq) {
		hashOpsUserEnterChatChannelInfo.delete(ENTER_CHAT_CHANNEL, userSeq);
		setOpsChatRoomUserValue.remove(String.valueOf(channelSeq), userSeq);
		return channelSeq;
	}

	public Long getUserChatRoom(Long userSeq) {
		return hashOpsUserEnterChatChannelInfo.get(ENTER_CHAT_CHANNEL, userSeq);
	}

	public Long getChatRoomMembers(Long channelSeq) {
		// return setOpsChatRoomUserValue.members(String.valueOf(channelSeq));
		return Long.valueOf(valOpsRoomUserValue.get(String.valueOf(channelSeq)));
	}
}
