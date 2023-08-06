package com.ssafy.withview.config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ssafy.withview.entity.LoginEntity;
import com.ssafy.withview.repository.LoginRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {

	private final LoginRepository loginRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("PrincipalDetailsService 실행, username: {}", username);

		LoginEntity loginEntity = loginRepository.findByUserSeq(Long.parseLong(username))
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));
		log.debug("loginEntity: " + loginEntity.getUserSeq() + ", " + loginEntity.getPassword());

		return new PrincipalDetails(loginEntity);
	}
}
