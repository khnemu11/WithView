package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.exception.DuplicateException;
import com.ssafy.withview.exception.InvalidVerificationCodeException;
import com.ssafy.withview.dto.JoinDto;
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

	/**
	 * 회원가입 진행
	 * @param joinDto (아이디, 비밀번호, 닉네임, 이메일)
	 * @return ResponseEntity (true / false, 상태코드)
	 */
	@PostMapping("")
	public ResponseEntity<Map<String, Object>> join(@RequestBody JoinDto joinDto) {
		log.info("UserController: 회원가입 진행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		log.info("joinDto - id: {}", joinDto.getId());
		try {
			userService.join(joinDto);
			log.info("UserController: 회원가입 성공");
			resultMap.put("success", true);
			status = HttpStatus.CREATED;
		} catch (Exception e) {
			log.error("UserController: 회원가입 실패 {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 아이디 중복 검사
	 * @param id (중복검사할 아이디)
	 * @return ResponseEntity (true / false, 상태코드)
	 * @throws DuplicateException 아이디가 중복될 때
	 */
	@GetMapping("/check-id")
	public ResponseEntity<Map<String, Object>> checkDuplicateId(@RequestParam(value = "id") String id) {
		log.info("UserController - checkDuplicateId: 아이디 중복 검사 실행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			if (userService.checkDuplicateId(id)) {
				throw new DuplicateException("Duplicate Id");
			}
			log.info("UserController - checkDuplicateId: 사용 가능한 아이디입니다.");
			resultMap.put("success", true);
			status = HttpStatus.OK;
		} catch (DuplicateException e) {
			log.info("UserController - checkDuplicateId: 이미 사용중인 아이디입니다.");
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.CONFLICT;
		} catch (Exception e) {
			log.error("UserController - checkDuplicateId: {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 이메일 중복 검사 및 해당 이메일로 인증 코드 발송
	 * @param email (인증 받을 이메일)
	 * @return ResponseEntity(true / false, 상태코드)
	 * @throws DuplicateException 이메일이 중복될 때
	 */
	@GetMapping("/email/validate")
	public ResponseEntity<Map<String, Object>> getEmailValidationCode(@RequestParam(value = "email") String email) {
		log.info("UserController - getEmailValidationCode: 이메일 인증 요청");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			if (userService.checkDuplicateEmail(email)) {
				throw new DuplicateException("Duplicate Email");
			}
			log.info("UserController - getEmailValidationCode: 사용 가능한 이메일입니다.");
			emailService.sendEmail(email);
			log.info("UserController - getEmailValidationCode: 이메일 인증 코드 전송 완료");
			resultMap.put("success", true);
			status = HttpStatus.OK;
		} catch (DuplicateException e) {
			log.info("UserController - getEmailValidationCode: 이미 사용중인 이메일입니다.");
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.CONFLICT;
		} catch (Exception e) {
			log.error("UserController - getEmailValidationCode: {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 이메일 중복 검사 및 해당 이메일로 인증 코드 발송
	 * @param email (인증 받을 이메일)
	 * @param code (이메일로 발송된 인증 코드)
	 * @return ResponseEntity(true / false, 상태코드)
	 * @throws InvalidVerificationCodeException 인증코드가 일치하지 않을 때
	 */
	@GetMapping("/email/authenticate")
	public ResponseEntity<Map<String, Object>> checkAuthenticateCode(@RequestParam(value = "email") String email,
		@RequestParam(value = "code") String code) {
		log.info("UserController - checkAuthenticateCode: 이메일 인증 코드 검사 실행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			if (!emailService.checkEmailVerificationCode(email, code)) {
				throw new InvalidVerificationCodeException("Invalid code");
			}
			log.info("UserController - checkAuthenticateCode: 인증코드 일치");
			resultMap.put("success", true);
			status = HttpStatus.OK;
		} catch (InvalidVerificationCodeException e) {
			log.info("UserController - checkAuthenticateCode: 인증코드 불일치 {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.UNAUTHORIZED;
		} catch (Exception e) {
			log.error("UserController - checkAuthenticateCode: {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(resultMap, status);
	}
}
