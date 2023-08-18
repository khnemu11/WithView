package com.ssafy.withview.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.ssafy.withview.interceptor.AgentWebSocketHandlerDecoratorFactory;
import com.ssafy.withview.interceptor.StompHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

	private final StompHandler stompHandler;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/api/sub");
		config.setApplicationDestinationPrefixes("/api/pub");
	}

	// @Override
	// public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
	// 	registry.setMessageSizeLimit(1024*1000);
	// 	registry.setSendBufferSizeLimit(3*1024*1024);
	// 	registry.setSendBufferSizeLimit(20*10000);
	// }

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		registration.setDecoratorFactories(new AgentWebSocketHandlerDecoratorFactory());
	}
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/api/ws-stomp", "/api/ws-chat", "/api/ws-channel", "/api/ws-server").setAllowedOriginPatterns("*").withSockJS();
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompHandler);
	}
}
