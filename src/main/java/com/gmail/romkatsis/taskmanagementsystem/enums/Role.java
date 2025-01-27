package com.gmail.romkatsis.taskmanagementsystem.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
