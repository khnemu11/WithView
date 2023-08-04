package com.ssafy.withview.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.ssafy.withview.dto.ChannelDto;
import com.ssafy.withview.dto.ChannelValueDto;
import com.ssafy.withview.service.ChannelServiceImpl;
import com.ssafy.withview.service.WebSocketSubscribeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketSubscribeController {

	private final WebSocketSubscribeService webSocketSubscribeService;
	private final ChannelServiceImpl channelService;

	@MessageMapping("/server/enter")
	public void enterServerWebSocket(Long serverSeq) {
		List<ChannelValueDto> channelValueDtos = getChannelValueDtos(serverSeq);
		webSocketSubscribeService.sendChannelValue(channelValueDtos);
	}

	@MessageMapping("/channel/enter")
	public void enterChannelWebSocket(Long userSeq, Long channelSeq, Long serverSeq) {
		webSocketSubscribeService.enterChannel(userSeq, channelSeq);
		List<ChannelValueDto> channelValueDtos = getChannelValueDtos(serverSeq);
		webSocketSubscribeService.sendChannelValue(channelValueDtos);
	}

	@MessageMapping("/channel/leave")
	public void leaveChannelWebSocket(Long userSeq, Long channelSeq, Long serverSeq) {
		webSocketSubscribeService.leaveChannel(userSeq, channelSeq);
		List<ChannelValueDto> channelValueDtos = getChannelValueDtos(serverSeq);
		webSocketSubscribeService.sendChannelValue(channelValueDtos);
	}

	private List<ChannelValueDto> getChannelValueDtos(Long serverSeq) {
		List<ChannelDto> channels = channelService.findAllChannelsByServerSeq(serverSeq);
		List<ChannelValueDto> channelValueDtos = channels.stream()
			.map(channel -> {
				Map<Long, Set<Long>> channelValue = webSocketSubscribeService.getChannelValue(channel.getSeq());
				return ChannelValueDto.builder()
					.channelValue(channelValue)
					.serverSeq(serverSeq)
					.build();
			})
			.collect(Collectors.toList());
		return channelValueDtos;
	}
}
