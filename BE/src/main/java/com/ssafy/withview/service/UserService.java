package com.ssafy.withview.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.withview.repository.LoginRepository;
import com.ssafy.withview.repository.UserRepository;
import com.ssafy.withview.repository.dto.JoinDto;
import com.ssafy.withview.repository.entity.LoginEntity;
import com.ssafy.withview.repository.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final LoginRepository loginRepository;

	// 회원가입 시, login, users 테이블에 동시 저장
	// 현재는 login 테이블에만 저장
	// 유효성 검사 로직도 추가 예정
	@Transactional
	public Long join(JoinDto joinDto) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		UserEntity userEntity = userRepository.save(UserEntity.builder()
			.id(joinDto.getId())
			.email(joinDto.getEmail())
			.nickname(joinDto.getNickname())
			.build());

		LoginEntity loginEntity = loginRepository.save(LoginEntity.builder()
			.id(userEntity.getId())
			.password(encoder.encode(joinDto.getPassword()))
			.roles("ROLE_USER")
			.user_seq(userEntity.getSeq())
			.build());

		return loginEntity.getSeq();
	}

	@Transactional
	public boolean checkDuplicateId(String id) {
		return userRepository.existsById(id);
	}

	@Transactional
	public boolean checkDuplicateEmail(String email) {
		return userRepository.existsByEmail(email);
	}
}
