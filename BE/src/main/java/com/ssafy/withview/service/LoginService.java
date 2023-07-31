package com.ssafy.withview.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.withview.repository.LoginRepository;
import com.ssafy.withview.repository.dto.LoginDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

	@Autowired
	LoginRepository loginRepository;

	private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	@Transactional
	public Authentication login(LoginDto loginDto) {
		logger.info("LoginService: 로그인 진행");

		UsernamePasswordAuthenticationToken authenticationToken
			= new UsernamePasswordAuthenticationToken(loginDto.getId(), loginDto.getPassword());

		logger.info("login - authenticationToken: " + authenticationToken);

		// authenticate 매서드가 실행될 때 PrincipalDetailsService 에서 만든 loadUserByUsername 메서드가 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		logger.info("login2 - authentication: " + authentication);

		return authentication;
	}
}
