package com.ssafy.withview.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.constant.RoomType;
import com.ssafy.withview.dto.ChatRoom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ChannelChatRepository {

	// Redis CacheKeys
	private static final String CHAT_ROOMS = "CHAT_ROOM";
	public static final String USER_COUNT = "USER_COUNT";
	public static final String ENTER_INFO = "ENTER_INFO";
	@Resource(name = "redisTemplate")
	private HashOperations<String, Long, ChatRoom> hashOpsChatRoom;
	@Resource(name = "redisTemplate")
	private HashOperations<String, Long, ChatRoom> hashOpsEnterInfo;
	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valueOps;
	@Resource(name = "redisTemplate")
	private ValueOperations<String, Long> socketConnectedUserInfo;

	public void userConnectSocket(String id, Long userSeq) {
		socketConnectedUserInfo.set(id, userSeq);
	}

	public void userDisconnectSocket(String id, Long userSeq) {
		socketConnectedUserInfo.set(id, userSeq, 0, TimeUnit.SECONDS);
	}

	public List<ChatRoom> findAllRoom() {
		return hashOpsChatRoom.values(CHAT_ROOMS);
	}

	public ChatRoom findRoomById(String id) {
		return hashOpsChatRoom.get(CHAT_ROOMS, id);
	}

	/**
	 * 채팅방 생성: 서버간 채팅방 공유를 위해 redis hash에 저장한다.
	 */
	public ChatRoom createChatRoom(RoomType roomType, Long seq) {
		ChatRoom chatRoom = ChatRoom.create(roomType, seq);
		hashOpsChatRoom.put(roomType + "_CHAT", seq, chatRoom);
		return chatRoom;
	}

	public void deleteChatRoom(RoomType roomType, Long seq) {
		hashOpsChatRoom.delete(roomType + "_CHAT", seq);
	}

	public boolean findChatRoom(RoomType roomType, Long seq) {
		return hashOpsChatRoom.get(roomType + "_CHAT", seq) != null;
	}

	// 유저가 입장한 채팅방 ID와 유저 세션 ID 맵핑 정보 저장
	public void setUserEnterInfo(Long userSeq, ChatRoom chatRoom) {
		hashOpsEnterInfo.put(ENTER_INFO, userSeq, chatRoom);
	}

	// 유저 세션으로 입장해 있는 채팅방 ID 조회
	public ChatRoom getUserEnterRoomValue(String sessionId) {
		return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
	}

	// 유저 세션정보와 맵핑된 채팅방 ID 삭제
	public void removeUserEnterInfo(String sessionId) {
		hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
	}

	// 채팅방 유저수 조회
	public Long getUserCount(RoomType roomType, Long seq) {
		return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomType + "_" + seq)).orElse("0"));
	}

	public Long plusUserCount(RoomType roomType, Long seq) {
		return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomType + "_" + seq)).orElse(0L);
	}

	public Long minusUserCount(RoomType roomType, Long seq) {
		return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomType + "_" + seq))
			.filter(count -> count > 0)
			.orElse(0L);
	}
}
