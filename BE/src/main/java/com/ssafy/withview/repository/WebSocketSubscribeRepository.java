package com.ssafy.withview.repository;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
	private static final String USER_CONNECT_SESSION = "USER_CONNECT_SESSION_";
	private static final String USER_ENTER_SERVER_USERSEQ = "USER_ENTER_SERVER_USERSEQ_";

	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> hashOpsUserEnterChatChannelInfo;
	@Resource(name = "redisTemplate")
	private SetOperations<String, String> setOpsChatRoomMemberValue;
	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valOpsUserSessionInfo;
	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valOpsUserServerInfoOfNowChannel;
	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valOpsRoomUserValue;

	public Long userSubscribeChannelChat(Long userSeq, Long channelSeq, Long serverSeq) {
		valOpsUserServerInfoOfNowChannel.set(USER_ENTER_SERVER_USERSEQ + userSeq, String.valueOf(serverSeq));
		hashOpsUserEnterChatChannelInfo.put(ENTER_CHAT_CHANNEL, String.valueOf(userSeq), String.valueOf(channelSeq));
		setOpsChatRoomMemberValue.add(String.valueOf(channelSeq), String.valueOf(userSeq));
		return channelSeq;
	}

	public Long userUnsubscribeChannelChat(Long userSeq, Long channelSeq) {
		valOpsUserServerInfoOfNowChannel.set(USER_ENTER_SERVER_USERSEQ + userSeq, "", 1, TimeUnit.MILLISECONDS);
		hashOpsUserEnterChatChannelInfo.delete(ENTER_CHAT_CHANNEL, String.valueOf(userSeq));
		setOpsChatRoomMemberValue.remove(String.valueOf(channelSeq), String.valueOf(userSeq));
		return channelSeq;
	}

	public Long getUserEnterChannel(Long userSeq) {
		return Long.parseLong(
			Optional.ofNullable(hashOpsUserEnterChatChannelInfo.get(ENTER_CHAT_CHANNEL, String.valueOf(userSeq)))
				.orElse("0"));
	}

	public Set<Long> getChannelMembers(Long channelSeq) {
		return setOpsChatRoomMemberValue.members(String.valueOf(channelSeq))
			.stream()
			.map(Long::parseLong)
			.collect(Collectors.toSet());
	}

	public String userConnectSetSession(String simpSessionId, Long userSeq) {
		valOpsUserSessionInfo.set(USER_CONNECT_SESSION + simpSessionId, String.valueOf(userSeq));
		return simpSessionId;
	}

	public Long userDisconnect(String simpSessionId) {
		Long userSeq = Long.parseLong(
			Optional.ofNullable(valOpsUserSessionInfo.get(USER_CONNECT_SESSION + simpSessionId)).orElse("0"));
		Long channelSeq = getUserEnterChannel(userSeq);
		Long serverSeq = Long.parseLong(
			Optional.ofNullable(valOpsUserServerInfoOfNowChannel.get(USER_ENTER_SERVER_USERSEQ + userSeq)).orElse("0"));
		userUnsubscribeChannelChat(userSeq, channelSeq);
		valOpsUserSessionInfo.set(USER_CONNECT_SESSION + simpSessionId, "", 1, TimeUnit.MILLISECONDS);
		return serverSeq;
	}
}
