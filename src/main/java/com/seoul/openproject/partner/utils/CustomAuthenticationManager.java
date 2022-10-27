package com.seoul.openproject.partner.utils;

import com.seoul.openproject.partner.domain.model.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationManager {

    public Boolean userIdMatches(Authentication authentication, String userId) {
        User user = (User) authentication.getPrincipal();
        return user.getApiId().equals(userId);
    }
}
