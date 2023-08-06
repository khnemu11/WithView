package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.ssafy.withview.dto.UserSeqDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.ssafy.withview.dto.ChannelDto;
import com.ssafy.withview.dto.ChannelValueDto;
import com.ssafy.withview.service.ChannelServiceImpl;
import com.ssafy.withview.service.ChannelValueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChannelValueController {

	private final ChannelValueService channelValueService;
	private final ChannelServiceImpl channelService;

	@MessageMapping("/server/{serverSeq}/enter")
	public void enterServerWebSocket(@DestinationVariable(value = "serverSeq") Long serverSeq) {
		ChannelValueDto channelValueDto = getChannelValueDto(serverSeq);
		channelValueService.sendChannelValue(channelValueDto.toJson());
	}

	@MessageMapping("/channel/{serverSeq}/{channelSeq}/enter")
	public void enterChannelWebSocket(UserSeqDto userSeqDto, @DestinationVariable(value = "channelSeq") Long channelSeq, @DestinationVariable(value = "serverSeq") Long serverSeq) {
		channelValueService.enterChannel(userSeqDto.getUserSeq(), channelSeq, serverSeq);
		ChannelValueDto channelValueDto = getChannelValueDto(serverSeq);
		channelValueService.sendChannelValue(channelValueDto.toJson());
	}

	@MessageMapping("/channel/{serverSeq}/{channelSeq}/leave")
	public void leaveChannelWebSocket(UserSeqDto userSeqDto, @DestinationVariable(value = "channelSeq") Long channelSeq,	@DestinationVariable(value = "serverSeq") Long serverSeq) {
		channelValueService.leaveChannel(userSeqDto.getUserSeq(), channelSeq);
		ChannelValueDto channelValueDto = getChannelValueDto(serverSeq);
		channelValueService.sendChannelValue(channelValueDto.toJson());
	}

	private ChannelValueDto getChannelValueDto(Long serverSeq) {
		List<ChannelDto> channels = channelService.findAllChannelsByServerSeq(serverSeq);
		ChannelValueDto channelValueDto = new ChannelValueDto(serverSeq);
		channelValueDto.setChannelMember(new HashMap<>());
		for (ChannelDto channel : channels) {
			Long channelSeq = channel.getSeq();
			Set<Long> channelMemberValue = channelValueService.getChannelMemberValue(channelSeq);
			channelValueDto.getChannelMember().put(channelSeq, channelMemberValue);
		}
		return channelValueDto;
	}
}