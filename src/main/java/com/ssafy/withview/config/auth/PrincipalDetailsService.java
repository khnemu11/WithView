package com.ssafy.withview.config.auth;

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

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		LoginEntity loginEntity = loginRepository.findBySeq(1);
		// LoginEntity loginEntity = loginRepository.findById(username);
		// LoginEntity loginEntity = loginRepository.findById(username)
		// 	.orElseThrow(() -> new IllegalArgumentException("일치하는 로그인 정보가 없습니다. 로그인 ID: " + username));
		return new PrincipalDetails(loginEntity);
	}
}
