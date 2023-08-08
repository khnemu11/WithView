package com.ssafy.withview.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ssafy.withview.dto.ChannelChatDto;
import com.ssafy.withview.entity.ChannelChatMessageEntity;
import com.ssafy.withview.repository.ChannelChatMessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChannelChatService {

	private final ChannelChatMessageRepository channelChatMessageRepository;

	public List<ChannelChatDto> getChannelChatMessagesByPage(Long channelSeq, int page, int cnt) {
		return channelChatMessageRepository.findByChannelSeqOrderBySendTimeDesc(
				channelSeq, PageRequest.of(cnt * (page - 1), cnt * page))
			.stream()
			.map(ChannelChatMessageEntity::toDto)
			.collect(Collectors.toList());
	}

	@Transactional
	public void insertChannelChat(ChannelChatDto chat) {
		ChannelChatMessageEntity entity = ChannelChatDto.toEntity(chat);
		channelChatMessageRepository.save(entity);
	}

}
