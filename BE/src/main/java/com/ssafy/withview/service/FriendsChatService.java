package com.ssafy.withview.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ssafy.withview.dto.FriendsChatMessageDto;
import com.ssafy.withview.entity.FriendsChatMessageEntity;
import com.ssafy.withview.repository.FriendsChatMessageRepository;
import com.ssafy.withview.repository.FriendsChatRoomUserInfoRepository;
import com.ssafy.withview.repository.RedisTemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendsChatService {

	private final FriendsChatMessageRepository friendsChatMessageRepository;
	private final FriendsChatRoomUserInfoRepository friendsChatRoomUserInfoRepository;
	private final RedisTemplateRepository redisTemplateRepository;

	public List<FriendsChatMessageDto> getFriendsChatMessagesByPage(Long friendsChatRoomSeq, int page) {
		return friendsChatMessageRepository.findByFriendsChatRoomSeqOrderBySendTimeDesc(friendsChatRoomSeq,
				PageRequest.of(100 * (page - 1), 100 * page))
			.stream()
			.map(FriendsChatMessageEntity::toDto)
			.collect(Collectors.toList());
	}

	public FriendsChatMessageDto getLastFriendsChatMessage(Long friendsChatSeq) {
		return FriendsChatMessageEntity.toDto(
			friendsChatMessageRepository.findTopByFriendsChatRoomSeqOrderBySendTimeDesc(friendsChatSeq));
	}

	public Long getUnreadFriendsChatMessageCount(Long friendsChatSeq, Long userSeq, Long lastMessageSeq) {
		Long lastReadMessageSeq = Optional.ofNullable(
			friendsChatRoomUserInfoRepository.findTopByFriendsChatRoomEntitySeqAndUserSeq(
				friendsChatSeq, userSeq).getLastReadMessageSeq()).orElse(0L);

		return lastMessageSeq - lastReadMessageSeq;
	}

	@Transactional
	public void insertFriendsChat(FriendsChatMessageDto chat) {
		FriendsChatMessageEntity entity = FriendsChatMessageDto.toEntity(chat);
		friendsChatMessageRepository.save(entity);
	}

	public Long setFriendsChatRoomLastMessageSeq(Long friendsChatRoomSeq) {
		Long messageSeq = Optional.ofNullable(friendsChatMessageRepository.findTopByFriendsChatRoomSeqOrderBySendTimeDesc(
			friendsChatRoomSeq).getMessageSeq()).orElse(1L);
		redisTemplateRepository.setFriendsChatRoomLastMessageSeq(friendsChatRoomSeq, messageSeq);
		return messageSeq;
	}

	public Long getFriendsChatRoomLastMessageSeq(Long friendsChatRoomSeq) {
		return redisTemplateRepository.getFriendsChatRoomLastMessageSeq(friendsChatRoomSeq);
	}
}
