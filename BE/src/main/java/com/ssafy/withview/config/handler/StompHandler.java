package com.ssafy.withview.config.handler;

import com.ssafy.withview.dto.ChannelDto;
import com.ssafy.withview.dto.ChannelValueDto;
import com.ssafy.withview.service.ChannelServiceImpl;
import com.ssafy.withview.service.ChannelValueService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.ssafy.withview.repository.FriendRepository;
import com.ssafy.withview.repository.ServerRepository;
import com.ssafy.withview.repository.WebSocketSubscribeRepository;
import com.ssafy.withview.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

	private final WebSocketSubscribeRepository webSocketSubscribeRepository;
	private final ChannelValueService channelValueService;
	private final ChannelServiceImpl channelService;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		if (StompCommand.CONNECT == accessor.getCommand()) {

			String simpSessionId = webSocketSubscribeRepository.userConnectSetSession((String) message.getHeaders().get("simpSessionId"), Long.valueOf(accessor.getFirstNativeHeader("userSeq")));
//			log.info("connect accessor simpSessionId: {}", simpSessionId);
//			log.info("connect message userSeq: {}", accessor.getFirstNativeHeader("userSeq"));

		}  else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료

//			log.info("disconnect accessor simpSessionId: {}", (String) message.getHeaders().get("simpSessionId"));
			Long serverSeq = webSocketSubscribeRepository.userDisconnect((String) message.getHeaders().get("simpSessionId"));
//			log.info("disconnect serverSeq: {}", serverSeq);
			if (serverSeq == 0L) {
				return message;
			}
			ChannelValueDto channelValueDto = getChannelValueDto(serverSeq);
			channelValueService.sendChannelValue(channelValueDto.toJson());
//			log.info("disconnect message: {}", message.toString());

		}
		return message;
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