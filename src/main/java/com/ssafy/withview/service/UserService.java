package com.ssafy.withview.service;

import com.ssafy.withview.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.withview.repository.UserRepository;
import com.ssafy.withview.repository.dto.LoginDto;
import com.ssafy.withview.repository.entity.LoginEntity;

@Service
public class UserService {

	@Autowired
	LoginRepository loginRepository;

	// 회원가입 시, login, users 테이블에 동시 저장
	// 현재는 login 테이블에만 저장
	// 유효성 검사 로직도 추가 예정
	@Transactional
	public LoginDto join(LoginDto loginDto) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		LoginEntity loginEntity = loginRepository.save(LoginEntity.builder()
			.id(loginDto.getId())
			.password(encoder.encode(loginDto.getPassword()))
			.roles("ROLE_USER")
			.build());

		return LoginDto.builder()
			.id(loginEntity.getId())
			.password(loginEntity.getPassword())
			.roles(loginEntity.getRoles())
			.build();
	}
}
