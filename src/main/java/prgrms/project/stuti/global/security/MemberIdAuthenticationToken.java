package prgrms.project.stuti.global.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

public class MemberIdAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final Long principal;

	private Object credentials;

	public MemberIdAuthenticationToken(Long principal, Object credentials) {
		super(null);
		this.principal = principal;
		this.credentials = credentials;
		setAuthenticated(false);
	}

	public MemberIdAuthenticationToken(Long principal, Object credentials,
		Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		super.setAuthenticated(true); // must use super, as we override
	}

	public static MemberIdAuthenticationToken unauthenticated(Long principal, Object credentials) {
		return new MemberIdAuthenticationToken(principal, credentials);
	}

	public static MemberIdAuthenticationToken authenticated(Long principal, Object credentials,
		Collection<? extends GrantedAuthority> authorities) {
		return new MemberIdAuthenticationToken(principal, credentials, authorities);
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Long getPrincipal() {
		return this.principal;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		Assert.isTrue(!isAuthenticated,
			"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		super.setAuthenticated(false);
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		this.credentials = null;
	}

}
