package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.dto.JwtDto;
import com.ssafy.withview.dto.LoginDto;
import com.ssafy.withview.dto.UserDto;
import com.ssafy.withview.service.JwtService;
import com.ssafy.withview.service.LoginService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

	private final LoginService loginService;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDto loginDto) {
		log.info("LoginController: 로그인 진행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = null;
		try {
			Authentication authentication = loginService.login(loginDto);
			if (authentication != null) {
				// JWT 정보
				JwtDto jwtDto = jwtService.generateToken(authentication);
				log.info("AccessToken: {}", jwtDto.getAccessToken());
				log.info("RefreshToken: {}", jwtDto.getRefreshToken());
				resultMap.put("JWT", jwtDto);
				// seq, nickname, profileImgSearchName 정보
				UserDto userDto = loginService.getUserInfo(loginDto);
				log.info("UserInfo: {}", userDto);
				resultMap.put("UserInfo", userDto);
				log.info("LoginController: 로그인 성공");
				resultMap.put("success", true);
				status = HttpStatus.CREATED;
			}
		} catch (Exception e) {
			log.error("LoginController: 로그인 실패 {}", e.getMessage());
			resultMap.put("success", false);
			status = HttpStatus.ACCEPTED;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	@PostMapping("/logout")
	public ResponseEntity<Map<String, Object>> logout(@PathVariable Long seq) {
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		resultMap.put("success", true);
		return new ResponseEntity<>(resultMap, status);
	}
}
