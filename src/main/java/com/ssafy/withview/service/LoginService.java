package com.ssafy.withview.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.withview.repository.LoginRepository;
import com.ssafy.withview.repository.dto.LoginDto;
import com.ssafy.withview.repository.entity.LoginEntity;

@Service
public class LoginService {

	@Autowired
	LoginRepository loginRepository;

	private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

	@Transactional
	public LoginDto login(LoginDto loginDto) {
		logger.info("LoginService: 로그인 진행");

		LoginEntity loginEntity = loginRepository.findById(loginDto.getId())
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if (encoder.matches(loginDto.getPassword(), loginEntity.getPassword())) {
			return LoginDto.builder()
				.id(loginEntity.getId())
				.password(loginEntity.getPassword())
				.build();
		} else {
			logger.error("아이디와 비밀번호가 일치하지 않습니다.");
			return null;
		}
	}
}
