package partner42.modulecommon.domain.model.user;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RoleEnum {
    ROLE_USER, ROLE_ADMIN;

    @JsonCreator
    public static RoleEnum from(String s) {
        String target = s.toUpperCase();
        return RoleEnum.valueOf(target);
    }
}
