package com.ssafy.withview.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.AmazonS3;
import com.ssafy.withview.repository.UserRepository;
import com.ssafy.withview.repository.UserServerRepository;
import com.ssafy.withview.repository.entity.UserEntity;
import com.ssafy.withview.repository.entity.UserServerEntity;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import com.ssafy.withview.repository.ChannelRepository;
import com.ssafy.withview.repository.ServerRepository;
import com.ssafy.withview.repository.dto.ChannelDto;
import com.ssafy.withview.repository.dto.ServerDto;
import com.ssafy.withview.repository.entity.ChannelEntity;
import com.ssafy.withview.repository.entity.ServerEntity;

import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {
	private final ServerRepository serverRepository;
	private final ChannelRepository channelRepository;
	private final UserServerRepository userServerRepository;
	private final UserRepository userRepository;

	@Override
	public List<ChannelDto> findAllChannelsByServerSeq(int seq) {
		List<ChannelEntity> entityList = channelRepository.findAllByServerSeq(seq);
		return entityList.stream().map(ChannelEntity::toDto).collect(Collectors.toList());
	}

	@Override
	public ChannelDto findChannelByName(String channelName) {
		return null;
	}

	@Transactional
	@Override
	public ServerDto insertServer(ServerDto serverDto) {
		ServerEntity serverEntity = ServerDto.toEntity(serverDto);
		return ServerEntity.toDto(serverRepository.save(serverEntity));
	}

	@Override
	public ServerDto updateServer(ServerDto serverDto) {
		ServerEntity serverEntity = serverRepository.findBySeq(serverDto.getSeq());

		if(serverEntity == null){
			return null;
		}
//		serverRepository.save(serverDto);



		return null;
	}

	@Override
	public ServerDto findServerBySeq(long serverSeq) {

		return ServerEntity.toDto(serverRepository.findBySeq(serverSeq));
	}

	@Override
	public List<ServerDto> findAllServerByUserSeq(long userSeq) {
		List<ServerDto> userServerDtoList = new ArrayList<>();
		UserEntity userEntity = userRepository.findBySeq(userSeq);
		List<UserServerEntity> userServerEntityList= userServerRepository.findAllServerByUserEntity(userEntity);

		for(UserServerEntity userServerEntity : userServerEntityList){
			userServerDtoList.add(ServerEntity.toDto(userServerEntity.getServerEntity()));
		}

		return userServerDtoList;
	}
}
