package com.ssafy.withview.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.withview.constant.Role;
import com.ssafy.withview.dto.UserDto;
import com.ssafy.withview.entity.LoginEntity;
import com.ssafy.withview.entity.UserEntity;
import com.ssafy.withview.repository.LoginRepository;
import com.ssafy.withview.repository.RefreshTokenRepository;
import com.ssafy.withview.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

	private final UserRepository userRepository;
	private final LoginRepository loginRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	/**
	 * 로그인 후 반환할 유저 정보를 얻어온다.
	 *
	 * @param id (로그인 id)
	 * @return UserDto (pk 값, 닉네임, 프로필 이미지)
	 */
	@Transactional
	public UserDto getUserInfo(String id) {
		log.debug("LoginService - getUserInfo 실행");

		UserEntity userEntity = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		return UserDto.builder()
			.seq(userEntity.getSeq())
			.nickname(userEntity.getNickname())
			.profileImgSearchName(userEntity.getProfileImgSearchName())
			.build();
	}

	/**
	 * 중복 로그인 방지
	 * @throws IllegalArgumentException 이미 로그인 중인 계정일 경우
	 */
	public void checkDuplicateLogin(Long seq) {
		if (refreshTokenRepository.existsById(String.valueOf(seq))) {
			throw new IllegalArgumentException("이미 로그인 중인 계정입니다.");
		}
	}

	/**
	 * 회원 탈퇴할 시, 로그인 불가
	 */
	public void hasBeenWithdrawn(Long seq) {
		UserEntity userEntity = userRepository.findBySeq(seq)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		if (userEntity.getDeleteTime() != null) {
			throw new IllegalArgumentException("일치하는 회원 정보가 없습니다.");
		}
	}

	/**
	 * 로그인 유저의 권한을 반환한다.
	 *
	 * @param userSeq (유저 pk 값)
	 * @return Role (권한)
	 */
	@Transactional
	public Role getRoles(Long userSeq) {
		log.debug("LoginService - getLoginInfo 실행");

		LoginEntity loginEntity = loginRepository.findByUserSeq(userSeq)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		return loginEntity.getRoles();
	}
}
