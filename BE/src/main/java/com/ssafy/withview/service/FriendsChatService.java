package com.ssafy.withview.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ssafy.withview.dto.FriendsChatMessageDto;
import com.ssafy.withview.dto.FriendsChatRoomLastReadDto;
import com.ssafy.withview.entity.FriendsChatMessageEntity;
import com.ssafy.withview.entity.FriendsChatRoomUserInfoEntity;
import com.ssafy.withview.repository.FriendsChatMessageRepository;
import com.ssafy.withview.repository.FriendsChatRoomRepository;
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
	private final FriendsChatRoomRepository friendsChatRoomRepository;
	private final RedisTemplateRepository redisTemplateRepository;

	public List<FriendsChatMessageDto> getFriendsChatMessagesByPage(Long friendsChatRoomSeq, int page) {
		return friendsChatMessageRepository.findByFriendsChatRoomSeqOrderByMessageSeqDesc(friendsChatRoomSeq,
				PageRequest.of(100 * (page - 1), 100 * page))
			.stream()
			.map(FriendsChatMessageEntity::toDto)
			.sorted((a, b) -> a.getMessageSeq().compareTo(b.getMessageSeq()))
			.collect(Collectors.toList());
	}

	public FriendsChatMessageDto getLastFriendsChatMessage(Long friendsChatSeq) {
		FriendsChatMessageEntity returnVal = friendsChatMessageRepository.findTopByFriendsChatRoomSeqOrderByMessageSeqDesc(
			friendsChatSeq);
		if (returnVal == null) {
			return FriendsChatMessageDto.builder()
				.friendsChatRoomSeq(0L)
				.message("")
				.messageSeq(0L)
				.fromUserSeq(0L)
				.toUserSeq(0L)
				.sendTime(friendsChatRoomRepository.findTopBySeq(friendsChatSeq).getCreatedTime())
				.build();
		}
		return FriendsChatMessageEntity.toDto(
			returnVal);
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

	public Long setFriendsChatRoomLastMessageSeqRedis(Long friendsChatRoomSeq) {
		Long messageSeq = Optional.ofNullable(friendsChatMessageRepository.findTopByFriendsChatRoomSeqOrderByMessageSeqDesc(
			friendsChatRoomSeq).getMessageSeq()).orElse(1L);
		redisTemplateRepository.setFriendsChatRoomLastMessageSeq(friendsChatRoomSeq, messageSeq);
		return messageSeq;
	}

	public Long getFriendsChatRoomLastMessageSeq(Long friendsChatRoomSeq) {
		return redisTemplateRepository.getFriendsChatRoomLastMessageSeq(friendsChatRoomSeq);
	}

	@Transactional
	public void setFriendsChatRoomLastMessageSeqJpa(Long friendsChatRoomSeq, Long userSeq, Long lastReadMessageSeq) {
		FriendsChatRoomUserInfoEntity entity = friendsChatRoomUserInfoRepository.findTopByFriendsChatRoomEntitySeqAndUserSeq(
			friendsChatRoomSeq, userSeq);
		FriendsChatRoomLastReadDto dto = FriendsChatRoomUserInfoEntity.toDto(entity);
		dto.setLastReadMessageSeq(lastReadMessageSeq);
		entity.updateLastReadMessageSeq(dto);
	}
}
