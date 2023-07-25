package com.ssafy.withview.service;

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
	UserRepository userRepository;

	// 현재: 로그인 table에만 저장
	@Transactional
	public LoginDto join(LoginDto loginDto) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		LoginEntity loginEntity = userRepository.save(LoginEntity.builder()
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
