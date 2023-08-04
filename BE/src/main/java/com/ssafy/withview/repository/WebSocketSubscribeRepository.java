package com.ssafy.withview.repository;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
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
	private static final String SERVER_CHANNEL_USER_VALUE = "SERVER_CHANNEL_USER_VALUE_";

	@Resource(name = "redisTemplate")
	private HashOperations<String, Long, Long> hashOpsUserEnterChatChannelInfo;
	@Resource(name = "redisTemplate")
	private SetOperations<Long, Long> setOpsChatRoomUserValue;

	public Long userSubscribeChatRoom(Long userSeq, Long channelSeq) {
		hashOpsUserEnterChatChannelInfo.put(ENTER_CHAT_CHANNEL, userSeq, channelSeq);
		setOpsChatRoomUserValue.add(channelSeq, userSeq);
		return channelSeq;
	}

	public void userUnsubscribeChatRoom(Long userSeq, Long channelSeq) {
		hashOpsUserEnterChatChannelInfo.delete(ENTER_CHAT_CHANNEL, userSeq);
		setOpsChatRoomUserValue.remove(channelSeq, userSeq);
	}

	public Long getUserChatRoom(Long userSeq) {
		return hashOpsUserEnterChatChannelInfo.get(ENTER_CHAT_CHANNEL, userSeq);
	}

	public Set<Long> getChatRoomMembers(Long channelSeq) {
		return setOpsChatRoomUserValue.members(channelSeq);
	}

	// public ChannelValueDto getServerChannelValue(Long serverSeq) {
	//
	// }
}
