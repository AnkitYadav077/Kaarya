
package com.Ankit.Kaarya.Security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class OtpAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final Object credentials;
    private final Long id;
    private final String role;


    public OtpAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.id = null;
        this.role = null;
        setAuthenticated(false);
    }


    public OtpAuthenticationToken(Long id, Object principal, Object credentials,
                                  String role,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.id = id;
        this.role = role;
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        if (getAuthorities() != null && !getAuthorities().isEmpty()) {
            return getAuthorities().iterator().next().getAuthority();
        }
        return null;
    }

}