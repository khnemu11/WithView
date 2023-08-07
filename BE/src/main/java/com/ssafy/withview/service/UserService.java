package com.ssafy.withview.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.ssafy.withview.config.jwt.Role;
import com.ssafy.withview.dto.JoinDto;
import com.ssafy.withview.dto.UserDto;
import com.ssafy.withview.entity.LoginEntity;
import com.ssafy.withview.entity.UserEntity;
import com.ssafy.withview.repository.LoginRepository;
import com.ssafy.withview.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final LoginRepository loginRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final AmazonS3 s3client;

	@Value(value = "${cloud.aws.s3.bucket}")
	private String bucketName;

	/**
	 * 회원가입 진행
	 * login table 정보 저장 (아이디, 비밀번호, 권한, user table 의 pk 값)
	 * user table 정보 버장 (아이디, 닉네임, 이메일)
	 *
	 * @param joinDto (아이디, 비밀번호, 닉네임, 이메일)
	 */
	@Transactional
	public void join(JoinDto joinDto) {
		UserEntity userEntity = userRepository.save(
			UserEntity.builder()
				.id(joinDto.getId())
				.email(joinDto.getEmail())
				.nickname(joinDto.getNickname())
				.build());

		loginRepository.save(
			LoginEntity.builder()
				.id(userEntity.getId())
				.password(bCryptPasswordEncoder.encode(joinDto.getPassword()))
				.roles(Role.ROLE_USER)
				.userSeq(userEntity.getSeq())
				.build());
	}

	/**
	 * 아이디 중복 검사
	 *
	 * @param id (검사할 id)
	 * @return Boolean (true: 중복, false: 중복x)
	 */
	@Transactional
	public Boolean checkDuplicateId(String id) {
		return userRepository.existsById(id);
	}

	/**
	 * 이메일 중복 검사
	 *
	 * @param email (검사할 email)
	 * @return Boolean (true: 중복, false: 중복x)
	 */
	@Transactional
	public Boolean checkDuplicateEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	/**
	 * 입력받은 id 와 email 이 같은 유저 정보인지 검사
	 *
	 * @param id    (확인할 id)
	 * @param email (확인할 email)
	 */
	public void findByIdAndEmail(String id, String email) {
		userRepository.findByIdAndEmail(id, email)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));
	}

	/**
	 * 마스킹한 아이디를 알려준다.
	 *
	 * @param email (아이디를 조회하기 위한 email)
	 * @return String (마스킹 된 아이디)
	 */
	public String getMaskedId(String email) {
		log.debug("UserService - getMaskedId 실행");

		UserEntity userEntity = userRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		String id = userEntity.getId();

		// id 의 절반을 * 로 마스킹
		int lengthToMask = id.length() / 2;
		String pattern = "^(.{" + lengthToMask + "}).*$";
		String replacement = "$1" + "*".repeat(lengthToMask);
		Pattern regexPattern = Pattern.compile(pattern);
		Matcher matcher = regexPattern.matcher(id);
		return matcher.replaceAll(replacement);
	}

	/**
	 * email 을 이용해 유저 정보를 가져온다.
	 * @param email (유저 email)
	 * @return UserDto (유저 pk 값)
	 */
	public UserDto getSeqByEmail(String email) {
		log.debug("UserService - getSeqByEmail 실행");

		UserEntity userEntity = userRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		return UserDto.builder()
			.seq(userEntity.getSeq())
			.build();
	}

	/**
	 * 프로필 이미지 및 상태 메시지 가져오기
	 *
	 * @param seq (확인할 유저 pk 값)
	 * @return UserDto (확인할 프로필 이미지, 확인할 상태 메시지)
	 */
	@Transactional
	public UserDto getProfile(Long seq) {
		log.debug("UserService - getProfile 실행");

		UserEntity userEntity = userRepository.findBySeq(seq)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		return UserDto.builder()
			.profileImgSearchName(userEntity.getProfileImgSearchName())
			.profileMsg(userEntity.getProfileMsg())
			.build();
	}

	/**
	 * 프로필 이미지 및 상태 메시지 변경
	 *
	 * @param seq           (변경할 유저 pk 값)
	 * @param multipartFile (변경할 프로필 이미지)
	 * @param profileMsg    (변경할 상태 메시지)
	 * @return UserDto (변경된 프로필 이미지, 변경된 상태 메시지)
	 */
	@Transactional
	public UserDto updateProfile(Long seq, MultipartFile multipartFile, String profileMsg) throws IOException {
		log.debug("UserService - updateProfile 실행");

		UserEntity userEntity = userRepository.findBySeq(seq)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		// 변경할 사진이 있을 경우, 기존 이미지 삭제 후 저장
		if (multipartFile != null) {
			log.debug("=== 저장할 이미지 생성 시작 ===");

			if (!s3client.doesBucketExistV2(bucketName)) {
				s3client.createBucket(bucketName);
			}

			String newOriginalName;
			File profileImgFile;
			UUID uuid = UUID.randomUUID();

			newOriginalName = multipartFile.getOriginalFilename();
			log.debug("new 원본 파일 이름: {}", newOriginalName);

			String extend = newOriginalName.substring(newOriginalName.lastIndexOf('.'));
			String newSearchName = uuid + extend;
			log.debug("new 랜덤 파일 이름: {}", newSearchName);

			profileImgFile = File.createTempFile(uuid.toString(), extend);
			FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), profileImgFile);
			log.debug("=== 저장할 이미지 생성 완료 ===");

			String oldSearchName = userEntity.getProfileImgSearchName();

			userEntity.updateProfileImg(newOriginalName, newSearchName);
			log.debug("User table: 이미지 이름 저장 완료");

			s3client.deleteObject(bucketName, "profile/" + oldSearchName);
			s3client.putObject(bucketName, "profile/" + newSearchName, profileImgFile);
			log.debug("Amazon S3 Bucket: 기존 이미지 삭제 및 새로운 이미지 저장 완료");
		}

		// 상태 메시지 변경
		userEntity.updateProfileMsg(profileMsg);

		return UserDto.builder()
			.profileImgSearchName(userEntity.getProfileImgSearchName())
			.profileMsg(userEntity.getProfileMsg())
			.build();
	}

	/**
	 * 회원 정보 수정 - 닉네임 변경
	 *
	 * @param seq      (변경할 유저 pk 값)
	 * @param nickname (변경할 닉네임)
	 * @return UserDto (변경된 닉네임)
	 */
	@Transactional
	public UserDto updateNickName(Long seq, String nickname) {
		log.debug("UserService - updateNickName 실행");

		UserEntity userEntity = userRepository.findBySeq(seq)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		userEntity.updateNickName(nickname);

		return UserDto.builder()
			.nickname(userEntity.getNickname())
			.build();
	}

	/**
	 * 회원 정보 수정 - 비밀번호 변경
	 *
	 * @param seq      (변경할 유저 pk 값)
	 * @param password (변경할 비밀번호)
	 */
	@Transactional
	public void updatePassword(Long seq, String password) {
		log.debug("UserService - updatePassword 실행");

		LoginEntity loginEntity = loginRepository.findByUserSeq(seq)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		loginEntity.updatePassword(bCryptPasswordEncoder.encode(password));
	}

	/**
	 * 회원 탈퇴
	 *
	 * @param seq (탈퇴할 유저 pk 값)
	 */
	@Transactional
	public void withdraw(Long seq) {
		log.debug("UserService - withdraw 실행");

		UserEntity userEntity = userRepository.findBySeq(seq)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		// DB 회원 탈퇴 처리
		LocalDateTime time = LocalDateTime.now();
		userEntity.withdraw(seq, time);
		log.debug("회원 탈퇴 신청 시간: {}", time);

		// 로그아웃 및 쿠키 제거
		SecurityContextHolder.clearContext();
	}
}
