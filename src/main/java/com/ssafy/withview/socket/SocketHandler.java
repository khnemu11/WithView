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
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SocketHandler extends TextWebSocketHandler {
	private static final Map<String,  List<WebSocketSession>> memberByChannel = new ConcurrentHashMap<>() ;
	private static final Map<String, String> stageByChannel = new ConcurrentHashMap<>() ;
	private static final Map<WebSocketSession, String> channelByUser = new ConcurrentHashMap<>() ;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println(session);
		String url = session.getUri().toString();
		String[] subQeury = url.toString().split("=");
		String channelId = subQeury[1];
		channelByUser.put(session,channelId);

		System.out.println(session.getId() + " 접속" + channelId);

		List<WebSocketSession> sessionList = memberByChannel.get(channelId);

		if(sessionList == null){
			sessionList = new ArrayList<>();
		}
		sessionList.add(session);
		memberByChannel.put(channelId,sessionList);

		System.out.println("=====현재 접속중인 세션=====");
		System.out.println(memberByChannel);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject)parser.parse((String)message.getPayload());
		System.out.println(jsonObject.toJSONString());
		if(jsonObject.get("type").equals("change")){
			System.out.println("변경된 스테이지");
			System.out.println(jsonObject.get("stage").toString());
			stageByChannel.put(jsonObject.get("channel").toString(),jsonObject.get("stage").toString());

			for (WebSocketSession member : memberByChannel.get(jsonObject.get("channel").toString())) {
				if (member.equals(session)) {
					continue;
				}
				System.out.println(member.getId() + " 에게 메시지 송신");
				try{
					synchronized (member) {
						member.sendMessage(message);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println(session.getId() + " 나감");
		String channel = channelByUser.get(session);
		memberByChannel.get(channel).remove(session);
		System.out.println("=====나머지 세션=====");
		System.out.println(memberByChannel);
	}
}