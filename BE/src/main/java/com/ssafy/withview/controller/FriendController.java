package com.ssafy.withview.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.dto.FriendDto;
import com.ssafy.withview.dto.UserDto;
import com.ssafy.withview.service.FriendService;
import com.ssafy.withview.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@Slf4j
public class FriendController {

	private final FriendService friendService;
	private final UserService userService;

	@GetMapping
	public ResponseEntity<?> getFollowingUsers(Long userSeq) {
		JSONObject result = new JSONObject();
		try {
			List<FriendDto> followingUsers = friendService.getFollowingUsers(userSeq);

			List<UserDto> followingUsersProfile = followingUsers.stream()
				.map(f -> userService.getProfile(f.getFollowedUserSeq()))
				.collect(Collectors.toList());

			result.put("success", true);
			result.put("userList", followingUsersProfile);
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
			result.put("follow", friendDto);
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
