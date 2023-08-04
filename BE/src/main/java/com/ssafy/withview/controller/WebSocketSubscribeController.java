package com.ssafy.withview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.service.WebSocketSubscribeService;

import lombok.RequiredArgsConstructor;

@RestController("/api/websocket")
@RequiredArgsConstructor
public class WebSocketSubscribeController {

	private final WebSocketSubscribeService webSocketSubscribeService;

	@PostMapping("/enter/channel")
	public ResponseEntity<?> enterChannelWebSocket(Long userSeq, Long channelSeq) {
		Long enterChannelSeq = webSocketSubscribeService.enterChannel(userSeq, channelSeq);
		return null;
	}
}
