package com.ssafy.withview.config.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ssafy.withview.repository.LoginRepository;
import com.ssafy.withview.repository.entity.LoginEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

	private final LoginRepository loginRepository;

	private static final Logger logger = LoggerFactory.getLogger(PrincipalDetailsService.class);

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("PrincipalDetailsService 실행");

		LoginEntity loginEntity = loginRepository.findById(username)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));
		logger.info("loginEntity: " + loginEntity.getId() + ", " + loginEntity.getPassword() + ", "
			+ loginEntity.getRoleList());
		
		return new PrincipalDetails(loginEntity);
	}
}
