package com.ssafy.withview.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ssafy.withview.dto.FriendsChatDto;
import com.ssafy.withview.entity.FriendsChatMessageEntity;
import com.ssafy.withview.repository.FriendsChatMessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendsChatService {

	private final FriendsChatMessageRepository friendsChatMessageRepository;

	public List<FriendsChatDto> getFriendsChatMessagesByPage(Long friendsChatSeq, int page, int cnt) {
		return friendsChatMessageRepository.findByFriendsChatSeqOrderBySendTimeDesc(
				friendsChatSeq, PageRequest.of(cnt * (page - 1), cnt * page))
			.stream()
			.map(FriendsChatMessageEntity::toDto)
			.collect(Collectors.toList());
	}

	@Transactional
	public void insertFriendsChat(FriendsChatDto chat) {
		FriendsChatMessageEntity entity = FriendsChatDto.toEntity(chat);
		friendsChatMessageRepository.save(entity);
	}
}
