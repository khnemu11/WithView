package com.ssafy.withview.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ssafy.withview.entity.LoginEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PrincipalDetails implements UserDetails {

	private final LoginEntity loginEntity;

	private static final Logger logger = LoggerFactory.getLogger(PrincipalDetails.class);
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> auths = new ArrayList<>();
		loginEntity.getRoleList().forEach(r -> {
			auths.add(() -> r);
		});
		return auths;
	}

	@Override
	public String getPassword() {
		return loginEntity.getPassword();
	}

	@Override
	public String getUsername() {
		return loginEntity.getId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
