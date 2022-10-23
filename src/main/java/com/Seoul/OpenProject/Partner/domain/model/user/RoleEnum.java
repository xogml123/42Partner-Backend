package com.Seoul.OpenProject.Partner.domain.model.user;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RoleEnum {
    ROLE_UNAUTHORIZED, ROLE_USER, ROLE_MANAGER, ROLE_ADMIN;

    @JsonCreator
    public static RoleEnum from(String s) {
        String target = s.toUpperCase();
        return RoleEnum.valueOf(target);
    }
}
