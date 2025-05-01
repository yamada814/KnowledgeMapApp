package com.example.demo.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.entity.User;

public class LoginUserDetails implements UserDetails {
	private final User user;

	public LoginUserDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public String getPassword() {
		// TODO 自動生成されたメソッド・スタブ
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO 自動生成されたメソッド・スタブ
		return user.getUsername();
	}

	// アカウントの期限切れ（期限切れなし）
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// アカウントのロック状態（ロックは使用しない）
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// パスワードの期限切れ（期限切れなし）
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// 有効なユーザー
	@Override
	public boolean isEnabled() {
		return true;
	}

}
