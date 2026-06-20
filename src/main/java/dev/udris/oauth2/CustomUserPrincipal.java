package dev.udris.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

import dev.udris.entity.User;

import java.io.Serializable;
import java.util.*;

public class CustomUserPrincipal implements UserDetails, OAuth2User, OidcUser, Serializable {

	private static final long serialVersionUID = 1L;
	private final Integer id;
	private final String username;
	private final String password;
	private final Set<GrantedAuthority> authorities;
	
	// OAuth, OIDC
    private Map<String, Object> attributes;
    private OidcIdToken idToken;
    private OidcUserInfo userInfo;

    public CustomUserPrincipal(Integer id, String username, String password, Set<GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	public static CustomUserPrincipal fromUser(User user) {
        return new CustomUserPrincipal(user.getId(), user.getUsername(), user.getPassword(),
        		Set.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }

    public static CustomUserPrincipal fromUser(User user, Map<String, Object> attributes) {
        CustomUserPrincipal principal = fromUser(user);
        principal.attributes = attributes;
        return principal;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username; 
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

 // ---------- OAUTH2 / OIDC ----------
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }


    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    public void setIdToken(OidcIdToken idToken) {
        this.idToken = idToken;
    }

    public void setUserInfo(OidcUserInfo userInfo) {
        this.userInfo = userInfo;
    }
    
    @Override
    public String getName() {
        return username;
    }

}

