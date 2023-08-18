package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.ssafy.withview.dto.LoginDto;
import com.ssafy.withview.dto.UserDto;
import com.ssafy.withview.exception.BadRequestException;
import com.ssafy.withview.exception.DuplicateException;
import com.ssafy.withview.exception.InvalidVerificationCodeException;
import com.ssafy.withview.service.EmailService;
import com.ssafy.withview.service.JwtService;
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

	private final JwtService jwtService;

	/**
	 * 회원가입 진행
	 *
	 * @param joinDto (아이디, 비밀번호, 닉네임, 이메일)
	 * @return ResponseEntity (true / false, 상태코드)
	 */
	@PostMapping("")
	public ResponseEntity<Map<String, Object>> join(@RequestBody JoinDto joinDto) {
		log.debug("UserController: 회원가입 진행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			userService.join(joinDto);
			resultMap.put("success", true);
			status = HttpStatus.CREATED;
			log.info("회원가입 성공 id: {}", joinDto.getId());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 회원가입 실패: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 아이디 중복 검사
	 *
	 * @param id (중복검사할 아이디)
	 * @return ResponseEntity (true / false, 상태코드)
	 * @throws DuplicateException 아이디가 중복될 때
	 */
	@GetMapping("/check-id")
	public ResponseEntity<Map<String, Object>> checkDuplicateId(@RequestParam(value = "id") String id) {
		log.debug("UserController - checkDuplicateId: 아이디 중복 검사 실행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			if (userService.checkDuplicateId(id)) {
				throw new DuplicateException("Duplicate Id");
			}
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.debug("사용 가능한 아이디입니다. id: {}", id);
		} catch (DuplicateException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.CONFLICT;
			log.info("[Error] 이미 사용중인 아이디입니다. id: {}", id);
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 아이디 중복 검사 실패: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 이메일 중복검사 및 인증 코드 발송 (회원가입, 아이디 찾기, 비밀번호 찾기)
	 *
	 * @param email (인증 받을 이메일)
	 * @param id    (비밀번호 찾기 할 때 필요한 아이디)
	 * @param var   (1: 회원가입 - 중복검사 필요, 2: 아이디,비밀번호 찾기 - 중복검사 불필요)
	 * @return ResponseEntity(true / false, 상태코드)
	 * @throws DuplicateException       이메일이 중복될 때
	 * @throws IllegalArgumentException id 와 email 로 조회되는 회원정보가 없을 때
	 * @throws BadRequestException      매개변수 불일치 (var 값이 없거나 1,2,3 이 아닐 때)
	 */
	@GetMapping("/email/validate")
	public ResponseEntity<Map<String, Object>> getEmailValidationCode(@RequestParam(value = "email") String email,
		@RequestParam(value = "id", required = false) String id,
		@RequestParam(value = "var") String var
	) {
		log.debug("UserController - getEmailValidationCode: 이메일 인증 요청");
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
				log.debug("사용 가능한 이메일입니다. email: {}", email);
			}
			if (var.equals("3")) {
				if (id == null) {
					throw new BadRequestException("BAD_REQUEST");
				}
				userService.findByIdAndEmail(id, email);
				log.debug("id 와 email 에 해당하는 유저 정보가 있습니다.");
			}
			emailService.sendEmail(email);
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.debug("이메일 인증 코드 전송 완료");
		} catch (DuplicateException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.CONFLICT;
			log.info("[Error] 이미 사용 중인 이메일입니다. email: {} ", email);
		} catch (IllegalArgumentException | BadRequestException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			log.info("[Error] 매개변수 불일치 또는 일치하는 정보가 없습니다: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 이메일 인증 코드 전송 실패: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 이메일 인증 코드 확인
	 *
	 * @param email (인증 받을 이메일)
	 * @param code  (이메일로 발송된 인증 코드)
	 * @param var   (2: 아이디 찾기, 3: 비밀번호 찾기)
	 * @return ResponseEntity(true / false, 상태코드, 마스킹 된 아이디 or seq)
	 * @throws InvalidVerificationCodeException 인증코드가 일치하지 않을 때
	 * @throws IllegalArgumentException         email 로 조회되는 회원정보가 없을 때
	 * @throws BadRequestException              매개변수 불일치 (var 값이 2,3이 아닐 때)
	 */

	@GetMapping("/email/authenticate")
	public ResponseEntity<Map<String, Object>> checkAuthenticateCode(@RequestParam(value = "email") String email,
		@RequestParam(value = "code") String code,
		@RequestParam(value = "var", required = false) String var) {
		log.debug("UserController - checkAuthenticateCode: 이메일 인증 코드 검사 실행");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			if (!emailService.checkEmailVerificationCode(email, code)) {
				throw new InvalidVerificationCodeException("Invalid code");
			}
			if (var != null && var.equals("2")) {
				String maskedId = userService.getMaskedId(email);
				resultMap.put("id", maskedId);
				log.debug("마스킹 된 id: {}", maskedId);
			}
			if (var != null && var.equals("3")) {
				UserDto userDto = userService.getSeqByEmail(email);
				resultMap.put("seq", userDto.getSeq());
				log.debug("비밀번호를 변경 할 회원 seq: {}", userDto);
			}
			if (var != null && !var.equals("2") && !var.equals("3")) {
				throw new BadRequestException("BAD_REQUEST");
			}
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.debug("인증코드 일치: {}", code);
		} catch (InvalidVerificationCodeException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.UNAUTHORIZED;
			log.info("[Error] 인증코드 만료 또는 불일치: {}", code);
		} catch (IllegalArgumentException | BadRequestException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			log.info("[Error] 매개변수 불일치 또는 일치하는 정보가 없습니다: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 이메일 인증 코드 검사 실패: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 마이페이지 - 프로필 이미지 및 상태메시지 수정 페이지, 상대 프로필 정보 확인
	 *
	 * @param seq (확인할 유저 pk 값)
	 * @return ResponseEntity(true / false, 상태코드, UserDto - 프로필 이미지 이름 및 상태 메시지)
	 * @throws IllegalArgumentException seq 로 조회되는 회원정보가 없을 때
	 */
	@GetMapping("/{seq}")
	public ResponseEntity<Map<String, Object>> getProfile(@PathVariable(value = "seq") Long seq) {
		log.debug("UserController - getProfile: 프로필 이미지 및 상태 메시지 정보 받기");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			UserDto userDto = userService.getProfile(seq);
			resultMap.put("success", true);
			resultMap.put("UserInfo", userDto);
			status = HttpStatus.OK;
			log.debug("프로필 정보 조회 성공. seq: {} ", seq);
		} catch (IllegalArgumentException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.NOT_FOUND;
			log.info("[Error] 일치하는 회원 정보가 없습니다. seq: {}", seq);
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 프로필 정보 조회 실패: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 마이페이지 - 프로필 이미지 및 상태 메시지 변경
	 *
	 * @param seq           (변경할 유저 pk 값)
	 * @param multipartFile (변경할 이미지)
	 * @param profileMsg    (변경할 상태 메시지)
	 * @return ResponseEntity(true / false, 상태코드, UserDto - 변경된 프로필 이미지 이름 및 상태 메시지)
	 * @throws IllegalArgumentException seq 로 조회되는 회원정보가 없을 때
	 * @throws BadRequestException      로그인 유저와 수정할 회원정보의 seq 값이 다름 or 매개변수 불일치
	 */
	@PostMapping("/{seq}")
	public ResponseEntity<Map<String, Object>> updateProfile(
		@PathVariable(value = "seq") Long seq,
		@RequestParam(value = "file", required = false) MultipartFile multipartFile,
		@RequestParam(value = "profileMsg") String profileMsg, HttpServletRequest request) {
		log.debug("UserController - updateProfile: 프로필 이미지 및 상태 메시지 변경");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			String accessToken = jwtService.resolveAccessToken(request.getHeader("Authorization"));
			log.debug("AccessToken: {}", accessToken);
			LoginDto loginDto = jwtService.getLoginInfo(accessToken);
			if (loginDto.getUserSeq() != seq) {
				throw new BadRequestException("BAD_REQUEST");
			}
			UserDto userDto = userService.updateProfile(seq, multipartFile, profileMsg);
			resultMap.put("success", true);
			resultMap.put("UserInfo", userDto);
			status = HttpStatus.OK;
			log.debug("프로필 수정 완료. seq: {} ", seq);
		} catch (IllegalArgumentException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.NOT_FOUND;
			log.info("[Error] 일치하는 회원 정보가 없습니다. seq: {}", e.getMessage());
		} catch (BadRequestException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			log.warn("[Error] 로그인 유저와 수정할 회원정보의 seq 값이 다름: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 프로필 수정 실패: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 마이페이지 - 닉네임 변경
	 *
	 * @param seq      (변경할 유저 pk 값)
	 * @param nickname (변경할 닉네임)
	 * @return ResponseEntity(true / false, 상태코드, UserDto - 변경된 닉네임)
	 * @throws IllegalArgumentException seq 로 조회되는 회원정보가 없을 때
	 * @throws BadRequestException      로그인 유저와 수정할 회원정보의 seq 값이 다름 or 매개변수 불일치
	 */
	@PutMapping("/{seq}/nickname")
	public ResponseEntity<Map<String, Object>> updateNickName(@PathVariable(value = "seq") Long seq,
		@RequestParam(value = "nickname") String nickname, HttpServletRequest request) {
		log.debug("UserController - updateNickName: 닉네임 변경");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			String accessToken = jwtService.resolveAccessToken(request.getHeader("Authorization"));
			log.debug("AccessToken: {}", accessToken);
			LoginDto loginDto = jwtService.getLoginInfo(accessToken);
			if (loginDto.getUserSeq() != seq) {
				throw new BadRequestException("BAD_REQUEST");
			}
			UserDto userDto = userService.updateNickName(seq, nickname);
			resultMap.put("success", true);
			resultMap.put("UserInfo", userDto);
			status = HttpStatus.OK;
			log.debug("닉네임 변경 완료 seq: {}", seq);
		} catch (IllegalArgumentException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.NOT_FOUND;
			log.info("[Error] 일치하는 회원 정보가 없습니다. seq: {}", e.getMessage());
		} catch (BadRequestException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			log.warn("[Error] 로그인 유저와 수정할 회원정보의 seq 값이 다름: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 닉네임 변경 실패: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 마이페이지 - 비밀번호 변경
	 *
	 * @param seq         (변경할 유저 pk 값)
	 * @param passwordMap (변경할 비밀번호)
	 * @param var         (1: 비밀번호 찾기, 2: 마이페이지 - 비밀번호 변경)
	 * @return ResponseEntity(true / false, 상태코드)
	 */
	@PutMapping("/{seq}/password")
	public ResponseEntity<Map<String, Object>> updatePassword(@PathVariable(value = "seq") Long seq,
		@RequestBody Map<String, String> passwordMap,
		@RequestParam(value = "var") String var,
		HttpServletRequest request) {
		log.debug("UserController - updatePassword: 비밀번호 변경");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			if (!var.equals("1") && !var.equals("2")) {
				throw new BadRequestException("BAD_REQUEST");
			}
			if (var.equals("2")) {
				String accessToken = jwtService.resolveAccessToken(request.getHeader("Authorization"));
				log.debug("AccessToken: {}", accessToken);
				LoginDto loginDto = jwtService.getLoginInfo(accessToken);
				if (loginDto.getUserSeq() != seq) {
					throw new BadRequestException("BAD_REQUEST");
				}
			}
			userService.updatePassword(seq, passwordMap.get("password"));
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.debug("비밀번호 변경 완료. seq: {}", seq);
		} catch (IllegalArgumentException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.NOT_FOUND;
			log.info("[Error] 일치하는 회원 정보가 없습니다. seq: {}", e.getMessage());
		} catch (BadRequestException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			log.warn("[Error] 로그인 유저와 수정할 회원정보의 seq 값이 다름: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 비밀번호 변경 실패: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 회원 탈퇴
	 *
	 * @param seq         (탈퇴할 유저 pk 값)
	 * @param passwordMap (유저 password)
	 * @return ResponseEntity(true / false, 상태코드)
	 */
	@DeleteMapping("/{seq}")
	public ResponseEntity<Map<String, Object>> withdraw(@PathVariable(value = "seq") Long seq,
		@RequestBody Map<String, String> passwordMap,
		HttpServletRequest request) {
		log.debug("UserController - withdraw: 회원탈퇴");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			String accessToken = jwtService.resolveAccessToken(request.getHeader("Authorization"));
			log.debug("AccessToken: {}", accessToken);
			LoginDto loginDto = jwtService.getLoginInfo(accessToken);
			if (loginDto.getUserSeq() != seq) {
				throw new BadRequestException("BAD_REQUEST");
			}
			userService.withdraw(seq, passwordMap.get("password"));
			jwtService.removeRefreshToken(seq);
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.info("회원탈퇴 완료. seq: {}", seq);
		} catch (IllegalArgumentException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.NOT_FOUND;
			log.info("[Error] 일치하는 회원 정보가 없습니다. seq: {}", e.getMessage());
		} catch (BadRequestException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			log.warn("[Error] 로그인 유저와 수정할 회원정보의 seq 값이 다르거나 비밀번호 불일치: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 회원탈퇴 실패: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}
}
