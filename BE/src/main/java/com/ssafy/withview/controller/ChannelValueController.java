package com.ssafy.withview.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.ssafy.withview.dto.ChannelValueDto;
import com.ssafy.withview.dto.UserSeqDto;
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
		ChannelValueDto channelValueDto = channelValueService.getChannelValueDto(serverSeq);
		channelValueService.sendChannelValue(channelValueDto.toJson());
	}

	@MessageMapping("/server/{serverSeq}/channel/{channelSeq}/enter")
	public void enterChannelWebSocket(UserSeqDto userSeqDto, @DestinationVariable(value = "channelSeq") Long channelSeq,
		@DestinationVariable(value = "serverSeq") Long serverSeq) {
		channelValueService.enterChannel(userSeqDto.getUserSeq(), channelSeq, serverSeq);
		ChannelValueDto channelValueDto = channelValueService.getChannelValueDto(serverSeq);
		channelValueService.sendChannelValue(channelValueDto.toJson());
	}

	@MessageMapping("/server/{serverSeq}/channel/{channelSeq}/leave")
	public void leaveChannelWebSocket(UserSeqDto userSeqDto, @DestinationVariable(value = "channelSeq") Long channelSeq,
		@DestinationVariable(value = "serverSeq") Long serverSeq) {
		channelValueService.leaveChannel(userSeqDto.getUserSeq(), channelSeq);
		ChannelValueDto channelValueDto = channelValueService.getChannelValueDto(serverSeq);
		channelValueService.sendChannelValue(channelValueDto.toJson());
	}
}