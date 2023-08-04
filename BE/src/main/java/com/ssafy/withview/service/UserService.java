package com.ssafy.withview.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.withview.repository.LoginRepository;
import com.ssafy.withview.repository.UserRepository;
import com.ssafy.withview.dto.JoinDto;
import com.ssafy.withview.entity.LoginEntity;
import com.ssafy.withview.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final LoginRepository loginRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * 회원가입 진행
	 * login table 정보 저장 (아이디, 비밀번호, 권한, user table 의 pk 값)
	 * user table 정보 버장 (아이디, 닉네임, 이메일)
	 * @param joinDto (아이디, 비밀번호, 닉네임, 이메일)
	 * @return login table 의 seq (pk 값)
	 */
	@Transactional
	public Long join(JoinDto joinDto) {
		UserEntity userEntity = userRepository.save(UserEntity.builder()
			.id(joinDto.getId())
			.email(joinDto.getEmail())
			.nickname(joinDto.getNickname())
			.build());

		LoginEntity loginEntity = loginRepository.save(LoginEntity.builder()
			.id(userEntity.getId())
			.password(bCryptPasswordEncoder.encode(joinDto.getPassword()))
			.roles("ROLE_USER")
			.user_seq(userEntity.getSeq())
			.build());

		return loginEntity.getSeq();
	}

	/**
	 * 아이디 중복 검사
	 * @param id (검사할 id)
	 * @return Boolean (true: 중복, false: 중복x)
	 */
	@Transactional
	public Boolean checkDuplicateId(String id) {
		return userRepository.existsById(id);
	}

	/**
	 * 이메일 중복 검사
	 * @param email (검사할 email)
	 * @return Boolean (true: 중복, false: 중복x)
	 */
	@Transactional
	public Boolean checkDuplicateEmail(String email) {
		return userRepository.existsByEmail(email);
	}
}
