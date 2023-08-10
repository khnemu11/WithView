package com.ssafy.withview.service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.ssafy.withview.dto.ChannelDto;
import com.ssafy.withview.dto.ChannelValueDto;
import com.ssafy.withview.dto.UserDto;
import com.ssafy.withview.entity.ChannelEntity;
import com.ssafy.withview.repository.ChannelRepository;
import com.ssafy.withview.repository.RedisTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelValueService {

	private final ChannelTopic channelValueChannelTopic;
	private final RedisTemplate redisTemplate;
	private final RedisTemplateRepository redisTemplateRepository;
	private final ChannelRepository channelRepository;
	private final UserService userService;

	public Long enterChannel(Long userSeq, Long channelSeq, Long serverSeq) {
		return redisTemplateRepository.userSubscribeChannelChat(userSeq, channelSeq, serverSeq);
	}

	public Long leaveChannel(Long userSeq, Long channelSeq) {
		return redisTemplateRepository.userUnsubscribeChannelChat(userSeq, channelSeq);
	}

	public Set<Long> getChannelMemberValue(Long channelSeq) {
		return redisTemplateRepository.getChannelMembers(channelSeq);
	}

	public ChannelValueDto getChannelValueDto(Long serverSeq) {
		List<ChannelDto> channels = channelRepository.findByServerEntitySeq(serverSeq).stream()
			.map(ChannelEntity::toDto)
			.collect(Collectors.toList());
		ChannelValueDto channelValueDto = new ChannelValueDto(serverSeq);
		channelValueDto.setChannelMember(new HashMap<>());
		for (ChannelDto channel : channels) {
			Long channelSeq = channel.getSeq();

			Set<UserDto> channelMemberUserDtos = getChannelMemberValue(channelSeq).stream()
				.map(userService::getProfile)
				.collect(Collectors.toSet());

			channelValueDto.getChannelMember().put(channelSeq, channelMemberUserDtos);
		}
		return channelValueDto;
	}

	/**
	 * 채팅방에 메시지 발송
	 */
	public void sendChannelValue(String channelValueJson) {
		redisTemplate.convertAndSend(channelValueChannelTopic.getTopic(), channelValueJson);
	}

	public String userConnectSetSession(String simpSessionId, Long userSeq) {
		return redisTemplateRepository.userConnectSetSession(simpSessionId, userSeq);
	}

	public Long userDisconnect(String simpSessionId) {
		return redisTemplateRepository.userDisconnect(simpSessionId);
	}
}
