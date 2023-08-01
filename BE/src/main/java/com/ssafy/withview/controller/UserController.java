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

import com.ssafy.withview.exception.DuplicateException;
import com.ssafy.withview.repository.dto.JoinDto;
import com.ssafy.withview.service.EmailService;
import com.ssafy.withview.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserService userService;
	private final EmailService emailService;

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

	@GetMapping("/check-id")
	public ResponseEntity<Map<String, Object>> checkDuplicateId(@RequestBody Map<String, String> bodyMap) {
		log.info("UserController: 아이디 중복 검사 실행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = null;
		try {
			if (userService.checkDuplicateId(bodyMap.get("id"))) {
				throw new DuplicateException("Duplicate Id");
			}
			log.info("UserController: 사용 가능한 아이디입니다.");
			resultMap.put("success", true);
			status = HttpStatus.OK;
		} catch (DuplicateException e) {
			log.error("UserController: 이미 사용중인 아이디입니다. {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.CONFLICT;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	@GetMapping("/email/validate")
	public ResponseEntity<Map<String, Object>> getEmailValidationCode(@RequestBody Map<String, String> bodyMap) {
		log.info("UserController: 이메일 인증 코드 발송");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = null;
		try {
			if (userService.checkDuplicateEmail(bodyMap.get("email"))) {
				throw new DuplicateException("Duplicate Email");
			}
			log.info("UserController: 사용 가능한 이메일입니다.");
			emailService.sendEmail(bodyMap.get("email"));
			log.info("UserController: 이메일 인증 코드 전송 성공");
			resultMap.put("success", true);
			status = HttpStatus.OK;
		} catch (DuplicateException e) {
			log.error("UserController: 이미 사용중인 이메일입니다. {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.CONFLICT;
		} catch (Exception e) {
			log.error("UserController: 이메일 인증 코드 전송 실패 {}", e.getMessage());
			resultMap.put("success", false);
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	@GetMapping("/email/authenticate")
	public ResponseEntity<Map<String, Object>> checkAuthenticateCode(@RequestBody Map<String, String> bodyMap) {
		log.info("UserController: 이메일 인증 코드 검사 실행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = null;
		try {
			if (!emailService.checkEmailVerificationCode(bodyMap.get("email"), bodyMap.get("code"))) {
				throw new Exception("Invalid code");
			}
			log.info("UserController: 인증코드 일치");
			resultMap.put("success", true);
			status = HttpStatus.OK;
		} catch (Exception e) {
			log.error("UserController: 인증코드 불일치 {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.UNAUTHORIZED;
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
