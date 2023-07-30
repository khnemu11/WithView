package com.ssafy.withview.service;

import com.ssafy.withview.repository.ChannelRepository;
import com.ssafy.withview.repository.ServerRepository;
import com.ssafy.withview.repository.UserRepository;
import com.ssafy.withview.repository.UserServerRepository;
import com.ssafy.withview.repository.dto.ChannelDto;
import com.ssafy.withview.repository.dto.ServerDto;
import com.ssafy.withview.repository.entity.ChannelEntity;
import com.ssafy.withview.repository.entity.ServerEntity;
import com.ssafy.withview.repository.entity.UserEntity;
import com.ssafy.withview.repository.entity.UserServerEntity;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

	@Transactional
	@Override
	public ServerDto updateServer(ServerDto serverDto) throws  Exception{
		ServerEntity serverEntity = serverRepository.findBySeq(serverDto.getSeq());
		System.out.println("대상 서버 " + serverEntity);
		if(serverEntity == null){
			throw new Exception("대상 서버가 없음");
		}

		serverEntity.update(serverDto);

		return ServerEntity.toDto(serverEntity);
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

	@Transactional
	@Override
	public JSONObject deleteServer(long serverSeq,long userSeq) {
		JSONObject result = new JSONObject();

		ServerEntity serverEntity = serverRepository.findBySeq(serverSeq);

		if(serverEntity == null){
			result.put("success",false);
			result.put("msg","해당 서버가 없습니다.");

			return result;
		}

		if(serverEntity.getHostSeq() != userSeq){
			result.put("success",false);
			result.put("msg","서버를 삭제할 권한이 없습니다.");

			return result;
		}
		result.put("img",serverEntity.getBackgroundImgSearchName());
		serverRepository.delete(serverEntity);

		result.put("success",true);
		result.put("msg","서버를 성공적으로 삭제하였습니다.");
		return result;
	}
}
