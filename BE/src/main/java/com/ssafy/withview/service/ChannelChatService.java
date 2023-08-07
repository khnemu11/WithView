package com.ssafy.withview.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ssafy.withview.dto.ChannelChatDto;
import com.ssafy.withview.entity.ChannelChatEntity;
import com.ssafy.withview.repository.ChannelChatRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChannelChatService {

	private final ChannelChatRepository channelChatRepository;

	public List<ChannelChatDto> getChannelChatMessagesByPage(Long channelSeq, int page, int cnt) {
		return channelChatRepository.findByChannelSeqOrderBySendTimeDesc(
				channelSeq, PageRequest.of(cnt * (page - 1), cnt * page))
			.stream()
			.map(ChannelChatEntity::toDto)
			.collect(Collectors.toList());
	}

	public void insertChannelChat(ChannelChatDto chat) {
		ChannelChatEntity entity = ChannelChatDto.toEntity(chat);
		channelChatRepository.save(entity);
	}

}
