package com.seoul.openproject.partner.controller.user;

import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.user.User;
import com.seoul.openproject.partner.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


//    @PreAuthorize("hasAuthority('todo.update') OR "
//        + "(hasAuthority('user.todo.update') AND @customAuthenticationManager.userIdMatches(authentication, #userId))")
    @Operation(summary = "특정 유저 userId로 조회", description = "특정 유저 userId로 조회")
    @GetMapping("/users/{userId}")
    public User.UserDto getUserById(@PathVariable String userId) {
        return userService.findById(userId);
    }

    //    @PreAuthorize("hasAuthority('todo.update') OR "
//        + "(hasAuthority('user.todo.update') AND @customAuthenticationManager.userIdMatches(authentication, #userId))")
    @Operation(summary = "특정 유저 email수정", description = "특정 유저 email 수정")
    @PatchMapping("/users/{userId}/email")
    public User.UserOnlyIdResponse getUserById(@PathVariable String userId,
        @Validated @Parameter @RequestBody User.UserUpdateRequest userUpdateRequest) {
        return userService.updateEmail(userId, userUpdateRequest);
    }



}
