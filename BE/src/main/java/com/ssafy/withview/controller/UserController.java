package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.repository.dto.JoinDto;
import com.ssafy.withview.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserService userService;

	@PostMapping("")
	public ResponseEntity<Map<String, Object>> join(@RequestBody JoinDto joinDto) {
		log.info("UserController: 회원가입 진행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = null;
		log.info("joinDto - id: {}", joinDto.getId());
		try {
			userService.join(joinDto);
			log.info("UserController: 회원가입 성공");
			resultMap.put("success", true);
			status = HttpStatus.CREATED;
		} catch (Exception e) {
			log.error("UserController: 회원가입 실패 {}", e.getMessage());
			resultMap.put("success", false);
			status = HttpStatus.ACCEPTED;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	@GetMapping("/test")
	public String test() {
		return "유저";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/test2")
	public String test2() {
		return "관리자";
	}
}
