package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
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

	/**
	 * 로그인
	 * @param loginDto (로그인 하려는 id, password)
	 * @return ResponseEntity (true / false, 상태코드, JWT, UserInfo)
	 */
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
		log.info("LoginController - login: 로그인 진행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			Authentication authentication = loginService.login(loginDto);
			if (authentication != null) {
				// JWT 발급 테스트
				JwtDto jwtDto = jwtService.generateToken(authentication);
				log.info("AccessToken: {}", jwtDto.getAccessToken());
				log.info("RefreshToken: {}", jwtDto.getRefreshToken());
				// resultMap.put("AccessToken", jwtDto.getAccessToken());
				resultMap.put("JWT", jwtDto);
				// seq, nickname, profileImgSearchName 정보
				UserDto userDto = loginService.getUserInfo(loginDto);
				log.info("UserInfo: {}", userDto);
				resultMap.put("UserInfo", userDto);
				// Cookie 생성
				ResponseCookie cookie = ResponseCookie.from("RefreshToken", jwtDto.getRefreshToken())
					.path("/")
					.sameSite("None")
					.httpOnly(true)
					.secure(true)
					.build();
				response.addHeader("Set-Cookie", cookie.toString());
				log.info("Cookie 생성 완료, 로그인 브랜치가 맞는가?");
				log.info("LoginController: 로그인 성공");
				resultMap.put("success", true);
				status = HttpStatus.CREATED;
			} else {
				throw new RuntimeException("로그인 실패");
			}
		} catch (Exception e) {
			log.error("LoginController: 로그인 실패 {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.ACCEPTED;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	@GetMapping("/cookie")
	public ResponseEntity<Map<String, Object>> testCookie(@CookieValue("RefreshToken") Cookie cookie) {
		log.info("LoginController - testCookie: http only cookie 확인");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		resultMap.put("success", true);
		resultMap.put("cookie", cookie);
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 로그아웃
	 * @param userDto (Long seq: 로그아웃 하려는 유저 pk 값)
	 * @return ResponseEntity (true / false, 상태코드)
	 */
	@PostMapping("/logout")
	public ResponseEntity<Map<String, Object>> logout(@RequestBody UserDto userDto) {
		log.info("LoginController - logout: 로그아웃 진행");
		log.info("seq: {}", userDto.getSeq());
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		resultMap.put("success", true);
		return new ResponseEntity<>(resultMap, status);
	}
}
