package com.ssafy.withview.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.ssafy.withview.dto.ChannelDto;
import com.ssafy.withview.dto.ChannelValueDto;
import com.ssafy.withview.service.ChannelServiceImpl;
import com.ssafy.withview.service.WebSocketSubscribeService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketSubscribeController {

	private final WebSocketSubscribeService webSocketSubscribeService;
	private final ChannelServiceImpl channelService;

	@MessageMapping("/server/{serverSeq}/entertest")
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
		// ChannelValueDto dto = ChannelValueDto.builder()
		// 	.serverSeq(serverSeq)
		// 	.channelSeq(channel)
		// 	.userSeq(user)
		// 	.build();
		// dto.channelMember = new HashMap<>();
		// for (int i = 0; i < 3; i++) {
		// 	dto.channelMember.put(channel.get(i), user.get(i));
		// }
		// log.info("구독 버튼?");
		// webSocketSubscribeService.sendChannelValue(dto);
	}

	@MessageMapping("/server/{serverSeq}/enter")
	public void enterServerWebSocket(@DestinationVariable(value = "serverSeq") Long serverSeq) {
		ChannelValueDto channelValueDto = getChannelValueDto(serverSeq);
		webSocketSubscribeService.sendChannelValue(channelValueDto.toJson());
	}

	@MessageMapping("/channel/{serverSeq}/{channelSeq}/enter")
	public void enterChannelWebSocket(// @Headers Map<String, MultiValueMap> headers,
		// @Headers MultiValueMap<String, Long> nativeHeaders,
		Temp tmp,
		@DestinationVariable(value = "channelSeq") Long channelSeq,
		@DestinationVariable(value = "serverSeq") Long serverSeq) {
		log.info("==============================");
		log.info("tmp: {}", tmp.getUserseq());
		// log.info("헤더: {}", headers.get("userseq"));
		// log.info("헤더 풀: {}", headers.keySet());
		// log.info("헤더 nativeHeaders: {}", headers.get("nativeHeaders"));
		// log.info("헤더 nativeHeaders Class: {}", headers.get("nativeHeaders"));
		// log.info("헤더 nativeHeaders userseq: {}", headers.get("nativeHeaders").getFirst("userseq"));
		// log.info("헤더 nativeHeaders userseq Class: {}", headers.get("nativeHeaders").getFirst("userseq").getClass());
		// log.info("nativeHeaders userseq: {}", nativeHeaders.getFirst("userseq"));
		// log.info("nativeHeaders userseq: {}", nativeHeaders.getFirst("userseq").getClass());
		// MultiValueMap nativeHeaders = (MultiValueMap)headers.get("nativeHeaders");

		// ArrayList userseq = (ArrayList)nativeHeaders.get("userseq");
		// log.info("롱롱타임어고 {}", userseq);

		// String json = null;
		// try {
		// 	json = new ObjectMapper().writeValueAsString(this);
		// } catch (JsonProcessingException e) {
		// 	throw new RuntimeException(e);
		// }

		webSocketSubscribeService.enterChannel(tmp.getUserseq(), channelSeq);
		ChannelValueDto channelValueDto = getChannelValueDto(serverSeq);
		webSocketSubscribeService.sendChannelValue(channelValueDto.toJson());
		log.info("==============================");
	}

	@MessageMapping("/channel/{serverSeq}/{channelSeq}/leave")
	public void leaveChannelWebSocket(Temp tmp, @DestinationVariable(value = "channelSeq") Long channelSeq,
		@DestinationVariable(value = "serverSeq") Long serverSeq) {
		webSocketSubscribeService.leaveChannel(tmp.getUserseq(), channelSeq);
		ChannelValueDto channelValueDto = getChannelValueDto(serverSeq);
		webSocketSubscribeService.sendChannelValue(channelValueDto.toJson());
	}

	private ChannelValueDto getChannelValueDto(Long serverSeq) {
		List<ChannelDto> channels = channelService.findAllChannelsByServerSeq(serverSeq);
		ChannelValueDto channelValueDto = new ChannelValueDto(serverSeq);
		channelValueDto.setChannelMember(new HashMap<>());
		for (ChannelDto channel : channels) {
			Long channelSeq = channel.getSeq();
			Set<Long> channelMemberValue = webSocketSubscribeService.getChannelMemberValue(channelSeq);
			channelValueDto.getChannelMember().put(channelSeq, channelMemberValue);
		}
		return channelValueDto;
	}
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Temp {
	private Long userseq;
}