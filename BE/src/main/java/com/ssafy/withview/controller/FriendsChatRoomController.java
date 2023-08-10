package com.ssafy.withview.controller;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.dto.FriendsChatMessageDto;
import com.ssafy.withview.dto.InsertFriendsChatRoomSeqsDto;
import com.ssafy.withview.service.FriendsChatRoomService;
import com.ssafy.withview.service.FriendsChatService;
import com.ssafy.withview.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class FriendsChatRoomController {

	private final FriendsChatRoomService friendsChatRoomService;
	private final FriendsChatService friendsChatService;
	private final UserService userService;

	@PostMapping
	public ResponseEntity<?> insertFriendsChatRoom(@RequestBody InsertFriendsChatRoomSeqsDto dto) {
		Long mySeq = dto.getMySeq();
		Long yourSeq = dto.getYourSeq();
		JSONObject result = new JSONObject();
		try {
			Long friendsChatRoomSeq = friendsChatRoomService.insertFriendsChatRoom(mySeq, yourSeq);
			log.info("새 1대1 채팅방 개설, 채팅방 번호: {}, 채팅방 생성 요청자: {}, 채팅 상대: {}", friendsChatRoomSeq, mySeq, yourSeq);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result = new JSONObject();
			result.put("success", false);
			result.put("msg", e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/{chatRoomSeq}")
	public ResponseEntity<?> getFriendsChatMessagesByPage(@PathVariable Long chatRoomSeq,
		@RequestParam Integer page) {

		JSONObject result = new JSONObject();
		try {
			List<FriendsChatMessageDto> friendsChatMessagesByPage = friendsChatService.getFriendsChatMessagesByPage(
				chatRoomSeq, page);
			result.put("messages", friendsChatMessagesByPage);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result = new JSONObject();
			result.put("success", false);
			result.put("msg", e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
