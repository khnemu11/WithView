package com.ssafy.withview.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ssafy.withview.repository.entity.LoginEntity;

import lombok.Getter;

@Getter
public class PrincipalDetails implements UserDetails {

	private LoginEntity loginEntity;

	public PrincipalDetails(LoginEntity loginEntity) {
		this.loginEntity = loginEntity;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> auths = new ArrayList<>();
		for (String role : loginEntity.getRoleList()) {
			auths.add(new GrantedAuthority() {
				@Override
				public String getAuthority() {
					return role;
				}
			});
		}
		return null;
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
