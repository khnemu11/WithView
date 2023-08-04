package com.ssafy.withview.service;

import org.springframework.stereotype.Service;

import com.ssafy.withview.repository.WebSocketSubscribeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebSocketSubscribeService {

	private final WebSocketSubscribeRepository webSocketSubscribeRepository;

	public Long enterChannel(Long userSeq, Long channelSeq) {
		return webSocketSubscribeRepository.userSubscribeChatRoom(userSeq, channelSeq);
	}
}
