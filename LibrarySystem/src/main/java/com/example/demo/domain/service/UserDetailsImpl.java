package com.example.demo.login.domain.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.login.domain.model.UserJPA;

public class UserDetailsImpl implements UserDetails{
	private final UserJPA user;

	public UserDetailsImpl(UserJPA user) {
		this.user = user;
	}
	public UserJPA getUser() {
		return user;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(){
		return AuthorityUtils.createAuthorityList("ROLE_" + this.user.getRoleName().name());
	}
	@Override
	public String getUsername() {
		return this.user.getMemberId();
	}
	@Override
	public String getPassword() {
		return this.user.getPassword();
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
