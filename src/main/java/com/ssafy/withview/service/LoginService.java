package com.ssafy.withview.service;

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
	private LoginRepository loginRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Transactional
	public LoginEntity join(LoginDto loginDto) {
		return loginRepository.save(LoginEntity.builder()
			.id(loginDto.getId())
			.password(bCryptPasswordEncoder.encode(loginDto.getPassword()))
			.roles("ROLE_USER")
			.build());
	}
}
