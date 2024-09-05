package com.diginamic.apiback.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    MANAGER,
    ADMIN,
    EMPLOYEE;

    @Override
    public String getAuthority() {
        return name();
    }
}
