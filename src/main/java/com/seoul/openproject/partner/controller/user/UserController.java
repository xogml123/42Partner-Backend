package com.seoul.openproject.partner.controller.user;

import com.seoul.openproject.partner.domain.model.user.User;
import com.seoul.openproject.partner.error.ErrorResult;
import com.seoul.openproject.partner.service.user.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @ExceptionHandler
    public ResponseEntity<ErrorResult> entityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResult.builder()
                .message(e.getMessage())
                .build());
    }
//    @PreAuthorize("hasAuthority('todo.update') OR "
//        + "(hasAuthority('user.todo.update') AND @customAuthenticationManager.userIdMatches(authentication, #userId))")
    @GetMapping("/users/{userId}")
    public User.UserDto getUserById(@PathVariable String userId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return userService.findById(userId);
    }


}
