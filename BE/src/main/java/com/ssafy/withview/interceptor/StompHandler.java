package com.ssafy.withview.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.ssafy.withview.dto.ChannelValueDto;
import com.ssafy.withview.service.ChannelValueService;
import com.ssafy.withview.service.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

	private final ChannelValueService channelValueService;
	private final JwtService jwtService;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		if (StompCommand.CONNECT == accessor.getCommand()) {

			channelValueService.userConnectSetSession(
				(String)message.getHeaders().get("simpSessionId"),
				Long.valueOf(accessor.getFirstNativeHeader("userSeq")));
			return message;
		}
		if (StompCommand.DISCONNECT == accessor.getCommand()) {

			Long serverSeq = channelValueService.userDisconnect(
				(String)message.getHeaders().get("simpSessionId"));
			String simpSessionId = (String)message.getHeaders().get("simpSessionId");
			if (serverSeq == 0L) {
				return message;
			}
			ChannelValueDto channelValueDto = channelValueService.getChannelValueDto(serverSeq);
			channelValueService.sendChannelValue(channelValueDto.toJson());
			return message;
		}
		return message;
	}
}