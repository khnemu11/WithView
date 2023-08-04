package com.ssafy.withview.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

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

	@MessageMapping("/server/{serverSeq}/enter")
	public void enterServerWebSocketTest(@DestinationVariable(value = "serverSeq") Long serverSeq) {
		// webSocketSubscribeService.enterChannel(1L, 8L);
		List<Long> channel = new ArrayList<>();
		channel.add(0L);
		channel.add(1L);
		channel.add(2L);
		List<Set<Long>> user = new ArrayList<>();
		user.add(new HashSet<>());
		user.add(new HashSet<>());
		user.add(new HashSet<>());
		user.get(0).add(0L);
		user.get(0).add(1L);
		user.get(0).add(2L);
		user.get(1).add(3L);
		user.get(1).add(4L);
		user.get(2).add(5L);
		user.get(2).add(6L);
		ChannelValueDto dto = ChannelValueDto.builder()
			.serverSeq(serverSeq)
			.channelSeq(channel)
			.userSeq(user)
			.build();
		dto.temp = new HashMap<>();
		for (int i = 0; i < 3; i++) {
			dto.temp.put(channel.get(i), user.get(i));
		}
		log.info("구독 버튼?");
		webSocketSubscribeService.sendChannelValue(dto);
	}

	@MessageMapping("/server/{serverSeq}/enter1")
	public void enterServerWebSocket(@DestinationVariable(value = "serverSeq") Long serverSeq) {
		webSocketSubscribeService.enterChannel(1L, 8L);
		webSocketSubscribeService.enterChannel(20L, 8L);
		webSocketSubscribeService.enterChannel(21L, 9L);
		webSocketSubscribeService.enterChannel(22L, 9L);
		webSocketSubscribeService.enterChannel(23L, 8L);
		// ChannelValueDto channelValueDto = getChannelValueDtos(serverSeq);
		// webSocketSubscribeService.sendChannelValue(channelValueDto);
	}

	@MessageMapping("/channel/enter")
	public void enterChannelWebSocket(Long userSeq, Long channelSeq, Long serverSeq) {
		webSocketSubscribeService.enterChannel(userSeq, channelSeq);
		// List<ChannelValueDto> channelValueDtos = getChannelValueDtos(serverSeq);
		// webSocketSubscribeService.sendChannelValue(channelValueDtos);
	}

	@MessageMapping("/channel/leave")
	public void leaveChannelWebSocket(Long userSeq, Long channelSeq, Long serverSeq) {
		webSocketSubscribeService.leaveChannel(userSeq, channelSeq);
		// List<ChannelValueDto> channelValueDtos = getChannelValueDtos(serverSeq);
		// webSocketSubscribeService.sendChannelValue(channelValueDtos);
	}

	// private ChannelValueDto getChannelValueDtos(Long serverSeq) {
	// 	List<ChannelDto> channels = channelService.findAllChannelsByServerSeq(serverSeq);
	// 	// List<ChannelValueDto> channelValueDtos = new ArrayList<>();
	// 	// for (ChannelDto channel : channels) {
	// 	// for (int i = 0; i < 1; i++) {
	// 	ChannelDto channel = channels.get(0);
	// 	Long channelSeq = channel.getSeq();
	// 	Long userSeq = webSocketSubscribeService.getChannelValue(channelSeq);
	// 	return ChannelValueDto.builder()
	// 		.channelSeq(channelSeq)
	// 		.serverSeq(serverSeq)
	// .userSeq(userSeq)
	// .build();
	// }
	// return channelValueDtos;
	// List<ChannelValueDto> channelValueDtos = channels.stream()
	// 	.map(channel -> {
	// 		Map<Long, Set<Long>> channelValue = webSocketSubscribeService.getChannelValue(channel.getSeq());
	// 		return ChannelValueDto.builder()
	// 			.channelValue(channelValue)
	// 			.serverSeq(serverSeq)
	// 			.build();
	// 	})
	// 	.collect(Collectors.toList());
	// return channelValueDtos;

	// return null;
	// }
}
