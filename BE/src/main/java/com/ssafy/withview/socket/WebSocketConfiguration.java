package com.ssafy.withview.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
	private final SocketHandler socketHandler;

	public WebSocketConfiguration(SocketHandler socketHandler) {
		this.socketHandler = socketHandler;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(socketHandler, "/api/socket")
			.setAllowedOriginPatterns("*")
			.withSockJS()
			.setClientLibraryUrl("https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.6.1/sockjs.min.js");
	}
}
