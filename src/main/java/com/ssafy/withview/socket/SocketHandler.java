package com.ssafy.withview.socket;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SocketHandler extends TextWebSocketHandler {
	//채널seq : 채널에 속해있는 클라이언트(세션)
	private static final Map<String, List<WebSocketSession>> memberByChannel = new ConcurrentHashMap<>();
	private static final Map<String, String> imageByChannel = new ConcurrentHashMap<>();
	private static final Map<String, String> videoByChannel = new ConcurrentHashMap<>();
	private static final Map<WebSocketSession, String> channelByUser = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println(session);
		String url = session.getUri().toString();
		String[] subQeury = url.toString().split("=");
		String channelId = subQeury[1];
		JSONObject jsonObject = new JSONObject();

		if (imageByChannel.get(channelId) != null) {
			jsonObject.put("image", imageByChannel.get(channelId));
		}
		if (videoByChannel.get(channelId) != null) {
			jsonObject.put("video", videoByChannel.get(channelId));
		}

		jsonObject.put("type", "join");

		channelByUser.put(session, channelId);

		List<WebSocketSession> sessionList = memberByChannel.get(channelId);

		if (sessionList == null) {
			sessionList = new ArrayList<>();
		}
		sessionList.add(session);
		memberByChannel.put(channelId, sessionList);

		session.sendMessage(new TextMessage(jsonObject.toJSONString()));
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		//소켓 에서 메시지 받을 때 내용 파싱하기
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject)parser.parse((String)message.getPayload());

		//if 문으로 json파일의 type으로 소켓 실행 구분
		if (jsonObject.get("type").equals("change")) {
			videoByChannel.put(jsonObject.get("channel").toString(), jsonObject.get("video").toString());
			imageByChannel.put(jsonObject.get("channel").toString(), jsonObject.get("image").toString());

			for (WebSocketSession member : memberByChannel.get(jsonObject.get("channel").toString())) {
				//자기 자신인 경우는 스킵
				if (member.equals(session)) {
					continue;
				}

				try {
					synchronized (member) {
						member.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	//소켓에서 연결이 끊어지면
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String channel = channelByUser.get(session);
		System.out.println(session.getId() + " 나감");

		memberByChannel.get(channel).remove(session);

		System.out.println("남은 사람");
		System.out.println(memberByChannel);

		if (memberByChannel.get(channel).size() == 0) {
			memberByChannel.remove(channel);
			videoByChannel.remove(channel);

		}
		System.out.println("=====나머지 세션=====");
		System.out.println(memberByChannel);
		System.out.println("=====저장된 에셋====");
		System.out.println("=====비디오====");
		System.out.println(videoByChannel);
		System.out.println("=====이미지====");
		System.out.println(imageByChannel);
	}
}