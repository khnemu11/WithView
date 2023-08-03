package com.ssafy.withview.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
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

		LoginEntity loginEntity = loginRepository.save(
			LoginEntity.builder()
				.id(userEntity.getId())
				.password(bCryptPasswordEncoder.encode(joinDto.getPassword()))
				.roles("ROLE_USER")
				.user_seq(userEntity.getSeq())
				.build());
	}

	/**
	 * 아이디 중복 검사
	 * @param id (검사할 id)
	 * @return boolean (true: 중복, false: 중복x)
	 */
	public boolean checkDuplicateId(String id) {
		return userRepository.existsById(id);
	}

	/**
	 * 이메일 중복 검사
	 * @param email (검사할 email)
	 * @return boolean (true: 중복, false: 중복x)
	 */
	public boolean checkDuplicateEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Transactional
	public UserDto getProfile(Long seq) {
		log.info("UserService - getProfile 실행");

		UserEntity userEntity = userRepository.findBySeq(seq);

		return UserDto.builder()
			.profileImgSearchName(userEntity.getProfileImgSearchName())
			.profileMsg(userEntity.getProfileMsg())
			.build();
	}

	/**
	 * 프로필 이미지 및 상태 메시지 변경
	 * @param seq (변경할 유저 pk 값)
	 * @param multipartFile (변경할 프로필 이미지)
	 * @param profileMsg (변경할 상태 메시지)
	 * @return UserDto (변경된 프로필 이미지, 변경된 상태 메시지)
	 */
	@Transactional
	public UserDto updateProfile(Long seq, MultipartFile multipartFile, String profileMsg) throws IOException {
		log.info("UserService - updateProfile 실행");

		UserEntity userEntity = userRepository.findBySeq(seq);

		// 변경할 사진이 있을 경우, 기존 이미지 삭제 후 저장
		if (multipartFile != null) {
			log.info("=== 저장할 이미지 생성 시작 ===");

			if (!s3client.doesBucketExistV2(bucketName)) {
				s3client.createBucket(bucketName);
			}

			String newOriginalName;
			File profileImgFile;
			UUID uuid = UUID.randomUUID();

			newOriginalName = multipartFile.getOriginalFilename();
			log.info("new 원본 파일 이름: {}", newOriginalName);

			String extend = newOriginalName.substring(newOriginalName.lastIndexOf('.'));
			String newSearchName = uuid + extend;
			log.info("new 랜덤 파일 이름: {}", newSearchName);

			profileImgFile = File.createTempFile(uuid.toString(), extend);
			FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), profileImgFile);
			log.info("=== 저장할 이미지 생성 완료 ===");

			String oldSearchName = userEntity.getProfileImgSearchName();

			userEntity.updateProfileImg(newOriginalName, newSearchName);
			log.info("User table: 이미지 이름 저장 완료");

			s3client.deleteObject(bucketName, "profile/" + oldSearchName);
			s3client.putObject(bucketName, "profile/" + newSearchName, profileImgFile);
			log.info("Amazon S3 Bucket: 기존 이미지 삭제 및 새로운 이미지 저장 완료");
		}

		// 상태 메시지 변경
		userEntity.updateProfileMsg(profileMsg);

		return UserDto.builder()
			.profileImgSearchName(userEntity.getProfileImgSearchName())
			.profileMsg(userEntity.getProfileMsg())
			.build();
	}
}
