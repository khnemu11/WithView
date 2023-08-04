package com.ssafy.withview.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

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

	// @MessageMapping("/server/test")
	// public void enterServerWebSocketTest(Long serverSeq) {
	// 	List<ChannelValueDto> channelValueDtos = getChannelValueDtos(serverSeq);
	// 	webSocketSubscribeService.sendChannelValue(channelValueDtos);
	// }

	@MessageMapping("/server/{serverSeq}/enter")
	public void enterServerWebSocket(@DestinationVariable(value = "serverSeq") Long serverSeq) {
		webSocketSubscribeService.enterChannel(1L, 8L);
		webSocketSubscribeService.enterChannel(20L, 8L);
		webSocketSubscribeService.enterChannel(21L, 9L);
		webSocketSubscribeService.enterChannel(22L, 9L);
		webSocketSubscribeService.enterChannel(23L, 8L);
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
