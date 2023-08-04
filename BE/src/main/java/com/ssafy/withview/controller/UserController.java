package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.withview.dto.JoinDto;
import com.ssafy.withview.dto.UserDto;
import com.ssafy.withview.exception.BadRequestException;
import com.ssafy.withview.exception.DuplicateException;
import com.ssafy.withview.exception.InvalidVerificationCodeException;
import com.ssafy.withview.exception.MismatchedUserInformationException;
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
	 * 이메일 중복검사 및 인증 코드 발송 (회원가입, 아이디 찾기, 비밀번호 찾기)
	 * @param email (인증 받을 이메일)
	 * @param id (비밀번호 찾기 할 때 필요한 아이디)
	 * @param var (1: 회원가입 - 중복검사 필요, 2: 아이디,비밀번호 찾기 - 중복검사 불필요)   
	 * @return ResponseEntity(true / false, 상태코드)
	 * @throws DuplicateException 이메일이 중복될 때
	 */
	@GetMapping("/email/validate")
	public ResponseEntity<Map<String, Object>> getEmailValidationCode(@RequestParam(value = "email") String email,
		@RequestParam(value = "id", required = false) String id,
		@RequestParam(value = "var") String var
	) {
		log.info("UserController - getEmailValidationCode: 이메일 인증 요청");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			if (!var.equals("1") && !var.equals("2") && !var.equals("3")) {
				throw new BadRequestException("BAD_REQUEST");
			}
			if (var.equals("1")) {
				if (userService.checkDuplicateEmail(email)) {
					throw new DuplicateException("Duplicate Email");
				}
				log.info("UserController - checkDuplicateEmail: 사용 가능한 이메일입니다.");
			}
			if (var.equals("3")) {
				if (id == null) {
					throw new BadRequestException("BAD_REQUEST");
				}
				if (!userService.findByIdAndEmail(id, email)) {
					throw new MismatchedUserInformationException("MismatchedUserInformation");
				}
				log.info("UserController - findByIdAndEmail: 같은 유저의 정보가 맞습니다.");
			}
			emailService.sendEmail(email);
			log.info("UserController - getEmailValidationCode: 이메일 인증 코드 전송 완료");
			resultMap.put("success", true);
			status = HttpStatus.OK;
		} catch (DuplicateException e) {
			log.info("UserController - getEmailValidationCode: {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.CONFLICT;
		} catch (MismatchedUserInformationException | BadRequestException e) {
			log.info("UserController - getEmailValidationCode: {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		} catch (Exception e) {
			log.error("UserController - getEmailValidationCode: {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 이메일 인증 코드 확인
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

	/**
	 * 마이페이지 - 프로필 이미지 및 상태메시지 수정 페이지, 상대 프로필 정보 확인
	 * @param seq (확인할 유저 pk 값)
	 * @return ResponseEntity(true / false, 상태코드, UserDto - 프로필 이미지 이름 및 상태 메시지)
	 */
	@GetMapping("/{seq}")
	public ResponseEntity<Map<String, Object>> getProfile(@PathVariable(value = "seq") Long seq) {
		log.info("UserController - getProfile: 프로필 이미지 및 상태 메시지 정보 받기");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			UserDto userDto = userService.getProfile(seq);
			log.info("프로필 정보: {}", userDto);
			resultMap.put("success", true);
			resultMap.put("UserInfo", userDto);
			status = HttpStatus.OK;
		} catch (Exception e) {
			log.error("UserController - updateProfile: {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 마이페이지 - 프로필 이미지 및 상태 메시지 변경
	 * @param seq (변경할 유저 pk 값)
	 * @param multipartFile (변경할 이미지)
	 * @param profileMsg (변경할 상태 메시지)
	 * @return ResponseEntity(true / false, 상태코드, UserDto - 변경된 프로필 이미지 이름 및 상태 메시지)
	 */
	@PostMapping("/{seq}")
	public ResponseEntity<Map<String, Object>> updateProfile(
		@PathVariable(value = "seq") Long seq,
		@RequestParam(value = "file", required = false) MultipartFile multipartFile,
		@RequestParam(value = "profileMsg") String profileMsg) {
		log.info("UserController - updateProfile: 프로필 이미지 및 상태 메시지 변경");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			UserDto userDto = userService.updateProfile(seq, multipartFile, profileMsg);
			log.info("UserController - updateProfile: 프로필 수정 완료");
			resultMap.put("success", true);
			resultMap.put("UserInfo", userDto);
			status = HttpStatus.OK;
		} catch (Exception e) {
			log.error("UserController - updateProfile: {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 마이페이지 - 닉네임 변경
	 * @param seq (변경할 유저 pk 값)
	 * @param nickname (변경할 닉네임)
	 * @return ResponseEntity(true / false, 상태코드, UserDto - 변경된 닉네임)
	 */
	@PutMapping("/{seq}/nickname")
	public ResponseEntity<Map<String, Object>> updateNickName(@PathVariable(value = "seq") Long seq,
		@RequestParam(value = "nickname") String nickname) {
		log.info("UserController - updateNickName: 닉네임 변경");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			UserDto userDto = userService.updateNickName(seq, nickname);
			log.info("UserController - updateNickName: 닉네임 변경 완료");
			resultMap.put("success", true);
			resultMap.put("UserInfo", userDto);
			status = HttpStatus.OK;
		} catch (Exception e) {
			log.error("UserController - updateNickName: {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 마이페이지 - 비밀번호 변경
	 * @param seq (변경할 유저 pk 값)
	 * @param password (변경할 비밀번호)
	 * @return ResponseEntity(true / false, 상태코드)
	 */
	@PutMapping("/{seq}/password")
	public ResponseEntity<Map<String, Object>> updatePassword(@PathVariable(value = "seq") Long seq,
		@RequestParam(value = "password") String password) {
		log.info("UserController - updatePassword: 비밀번호 변경");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			userService.updatePassword(seq, password);
			log.info("UserController - updatePassword: 비밀번호 변경 완료");
			resultMap.put("success", true);
			status = HttpStatus.OK;
		} catch (Exception e) {
			log.error("UserController - updatePassword: {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 회원 탈퇴
	 * @param seq (탈퇴할 유저 pk 값)
	 * @return ResponseEntity(true / false, 상태코드)
	 */
	@DeleteMapping("/{seq}")
	public ResponseEntity<Map<String, Object>> withdraw(@PathVariable(value = "seq") Long seq) {
		log.info("UserController - withdraw: 회원탈퇴");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			userService.withdraw(seq);
			log.info("UserController - withdraw: 회원탈퇴 완료");
			resultMap.put("success", true);
			status = HttpStatus.OK;
		} catch (Exception e) {
			log.error("UserController - withdraw: {}", e.getMessage());
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(resultMap, status);
	}
}
