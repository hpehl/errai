package org.jboss.errai.bus.server.security.auth;

import java.util.Set;

public class AuthSubject {
    protected String username;
    protected String fullname;
    protected Set<Object> roles;

    public AuthSubject(String username, String fullname, Set<Object> roles) {
        this.username = username;
        this.fullname = fullname;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public Set<Object> getRoles() {
        return roles;
    }
}
