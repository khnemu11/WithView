package com.ssafy.withview.controller;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.service.FriendsChatRoomService;
import com.ssafy.withview.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class FriendsChatRoomController {

	private final FriendsChatRoomService friendsChatRoomService;
	private final UserService userService;

	@PostMapping
	public ResponseEntity<?> insertFriendsChatRoom(Long mySeq, Long yourSeq) {
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

	// 이거 웹소켓으로 바꾸기
	// @GetMapping
	// public ResponseEntity<?> findFriendsChatRooms(@RequestParam("userSeq") Long userSeq) {
	// 	JSONObject result = new JSONObject();
	// 	try {
	// 		List<FriendsChatRoomsSeqDto> friendsChatRoomsByPartnerSeq = friendsChatRoomService.findFriendsChatRoomsByPartnerSeq(
	// 			userSeq);
	// 		List<FriendsChatRoomsUserInfoDto> friendsChatRoomsUserInfoDtos = friendsChatRoomsByPartnerSeq.stream()
	// 			.map(room -> FriendsChatRoomsUserInfoDto.builder()
	// 				.chatRoomSeq(room.getChatRoomSeq())
	// 				.userDto(userService.getProfile(room.getUserSeq()))
	// 				.build())
	// 			.collect(Collectors.toList());
	// 		result.put("chats", friendsChatRoomsUserInfoDtos);
	// 		result.put("success", true);
	// 	} catch (Exception e) {
	// 		e.printStackTrace();
	// 		result = new JSONObject();
	// 		result.put("success", false);
	// 		result.put("msg", e.getMessage());
	// 		return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	// 	}
	// 	return new ResponseEntity<>(result, HttpStatus.OK);
	// }
}
