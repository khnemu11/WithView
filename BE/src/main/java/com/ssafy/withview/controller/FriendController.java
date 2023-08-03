package com.ssafy.withview.controller;

import com.ssafy.withview.dto.FriendDto;
import com.ssafy.withview.dto.ServerDto;
import com.ssafy.withview.entity.FriendEntity;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.withview.service.FriendService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@Slf4j
public class FriendController {

	private final FriendService friendService;

	@GetMapping
	public ResponseEntity<?> getFollowingUsers(Long userSeq) {
		JSONObject result = new JSONObject();
		try {
			List<FriendDto> followingUsers = friendService.getFollowingUsers(userSeq);

			result.put("success", true);
			result.put("server", followingUsers);
		} catch (Exception e) {
			e.printStackTrace();
			result = new JSONObject();
			result.put("success", false);
			result.put("msg", userSeq + "친구 찾기를 실패했습니다.");
			return new ResponseEntity<JSONObject>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> followUser(Long followingUserSeq, Long followedUserSeq) {
		JSONObject result = new JSONObject();
		try {
			FriendDto friendDto = friendService.insertFollowUser(followingUserSeq, followedUserSeq);

			result.put("success", true);
			result.put("server", friendDto);
		} catch (Exception e) {
			e.printStackTrace();
			result = new JSONObject();
			result.put("success", false);
			result.put("msg", followedUserSeq + "친구 등록을 실패했습니다.");
			return new ResponseEntity<JSONObject>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}
}
