package dev.udris.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

	ADMIN, USER, MODERATOR;

	@Override
	public String getAuthority() {
		return "ROLE_" + name();
	}

}
