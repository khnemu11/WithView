package com.ssafy.withview.config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ssafy.withview.repository.LoginRepository;
import com.ssafy.withview.repository.dto.LoginDto;

@Service
public class PrincipalDetailsService implements UserDetailsService {

	private LoginRepository loginRepository;

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		LoginDto loginDto = loginRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 로그인 정보가 없습니다. 로그인 ID: " + id));
		return new PrincipalDetails(loginDto);
	}
}
