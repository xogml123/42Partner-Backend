package partner42.moduleapi.controller.user;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import partner42.moduleapi.dto.LoginResponseDto;
import partner42.moduleapi.dto.user.UserDto;
import partner42.moduleapi.dto.user.UserLoginRequest;
import partner42.moduleapi.dto.user.UserOnlyIdResponse;
import partner42.moduleapi.dto.user.UserUpdateRequest;
import partner42.moduleapi.service.user.UserService;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAuthority('user.read')")
    @Operation(summary = "특정 유저 userId로 조회", description = "특정 유저 userId로 조회")
    @GetMapping("/users/{userId}")
    public UserDto getUserById(@PathVariable String userId,
        @ApiParam(hidden = true) @AuthenticationPrincipal UserDetails user) {
        return userService.findById(userId, user.getUsername());
    }

    @PreAuthorize("hasAuthority('user.update')")
    @Operation(summary = "특정 유저 email수정", description = "특정 유저 email 수정")
    @PatchMapping("/users/{userId}/email")
    public UserOnlyIdResponse getUserById(@PathVariable String userId,
        @Validated @Parameter @RequestBody UserUpdateRequest userUpdateRequest,
        @ApiParam(hidden = true) @AuthenticationPrincipal UserDetails user) {
        return userService.updateEmail(userId, userUpdateRequest, user.getUsername());
    }

    @Operation(summary = "admin Form 로그인", description = "username password 각각 자기 인트라 아이디로 하면 됩니다!")
    @PostMapping("/auth/login")
    public LoginResponseDto fakeLogin(@Validated @ModelAttribute UserLoginRequest request) {
        throw new IllegalStateException(
            "This method shouldn't be called. It's implemented by Spring Security filters.");

    }

}
