package com.ssafy.withview.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ssafy.withview.repository.ChannelRepository;
import com.ssafy.withview.repository.ServerRepository;
import com.ssafy.withview.repository.dto.ChannelDto;
import com.ssafy.withview.repository.dto.ServerDto;
import com.ssafy.withview.repository.entity.ChannelEntity;
import com.ssafy.withview.repository.entity.ServerEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {
	private final ServerRepository serverRepository;
	private final ChannelRepository channelRepository;

	@Override
	public List<ChannelDto> findAllChannelsByServerSeq(int seq) {
		List<ChannelEntity> entityList = channelRepository.findAllByServerSeq(seq);
		return entityList.stream().map(ChannelEntity::toDto).collect(Collectors.toList());
	}

	@Override
	public ChannelDto findChannelByName(String channelName) {
		return null;
	}

	@Override
	public ServerDto insertServer(ServerDto serverDto) {
		ServerEntity serverEntity = ServerDto.toEntity(serverDto);
		return ServerEntity.toDto(serverRepository.save(serverEntity));
	}

	@Override
	public ServerDto findServerBySeq(long serverSeq) {
		return ServerEntity.toDto(serverRepository.findBySeq(serverSeq));
	}
}
