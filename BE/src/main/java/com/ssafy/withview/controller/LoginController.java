package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.constant.Role;
import com.ssafy.withview.dto.AccessTokenDto;
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
	 *
	 * @param loginDto (로그인 하려는 id, password)
	 * @return ResponseEntity (true / false, 상태코드, AccessToken, UserInfo - pk 값, 닉네임, 프로필 이미지)
	 * @throws IllegalArgumentException 조회되는 회원정보가 없을 때
	 */
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
		log.debug("LoginController - login: 로그인 진행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.CREATED;
		try {
			// 로그인 유저 정보
			UserDto userDto = loginService.getUserInfo(loginDto.getId());
			log.debug("UserInfo pk: {}", userDto.getSeq());
			Authentication authentication = jwtService.createAuthentication(userDto.getSeq(), loginDto.getPassword());
			if (authentication != null) {
				// AccessToken 발급
				Role role = loginService.getRoles(userDto.getSeq());
				AccessTokenDto accessTokenDto = jwtService.generateAccessToken(userDto.getSeq(), role);
				log.debug("AccessToken: {}", accessTokenDto.getAccessToken());
				// Cookie 생성
				String refreshToken = jwtService.generateRefreshToken(userDto.getSeq(), role);
				response.setHeader("Set-Cookie", jwtService.createCookie("RefreshToken", refreshToken).toString());
				log.debug("RefreshToken: {}", refreshToken);

				resultMap.put("AccessToken", accessTokenDto);
				resultMap.put("UserInfo", userDto);
				resultMap.put("success", true);
				log.info("로그인 성공. seq: {}", userDto.getSeq());
			}
		} catch (IllegalArgumentException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.ACCEPTED;
			log.info("[Error] 일치하는 회원 정보가 없습니다. seq: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 로그인 실패: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 로그아웃
	 *
	 * @param userDto (Long seq: 로그아웃 하려는 유저 pk 값)
	 * @return ResponseEntity (true / false, 상태코드)
	 */
	@PostMapping("/logout")
	public ResponseEntity<Map<String, Object>> logout(@RequestBody UserDto userDto, HttpServletResponse response) {
		log.debug("LoginController - logout: 로그아웃 진행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			log.debug("seq: {}", userDto.getSeq());
			// Redis 에서 RefreshToken 삭제
			jwtService.removeRefreshToken(userDto.getSeq());
			// Cookie 삭제
			jwtService.removeCookie("RefreshToken");
			response.setHeader("Set-Cookie", jwtService.removeCookie("RefreshToken").toString());
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.info("로그아웃 성공. seq: {}", userDto.getSeq());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 로그아웃 실패: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}
}
