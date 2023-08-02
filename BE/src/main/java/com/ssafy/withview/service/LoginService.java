package com.ssafy.withview.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.withview.repository.UserRepository;
import com.ssafy.withview.dto.LoginDto;
import com.ssafy.withview.dto.UserDto;
import com.ssafy.withview.entity.UserEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	private final UserRepository userRepository;

	@Transactional
	public Authentication login(LoginDto loginDto) {
		log.info("LoginService - login 실행");

		UsernamePasswordAuthenticationToken authenticationToken
			= new UsernamePasswordAuthenticationToken(loginDto.getId(), loginDto.getPassword());

		log.info("login - authenticationToken: {}", authenticationToken);

		// authenticate 매서드가 실행될 때 PrincipalDetailsService 에서 만든 loadUserByUsername 메서드가 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		log.info("login - authentication: {} ", authentication);

		return authentication;
	}

	@Transactional
	public UserDto getUserInfo(LoginDto loginDto) {
		log.info("LoginService - getUserInfo 실행");
		log.info("id: {}", loginDto.getId());
		UserEntity userEntity = userRepository.findById(loginDto.getId())
			.orElseThrow(() -> new RuntimeException("일치하는 회원 정보가 없습니다."));
		return UserDto.builder()
			.seq(userEntity.getSeq())
			.nickname(userEntity.getNickname())
			.profileImgSearchName(userEntity.getProfileImgSearchName())
			.build();
	}
}
