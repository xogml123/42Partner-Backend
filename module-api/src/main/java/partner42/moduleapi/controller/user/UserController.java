package partner42.moduleapi.controller.user;

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
import partner42.moduleapi.dto.user.UserDto;
import partner42.moduleapi.dto.user.UserOnlyIdResponse;
import partner42.moduleapi.dto.user.UserUpdateRequest;
import partner42.moduleapi.service.user.UserService;

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
    public UserDto getUserById(@PathVariable String userId) {
        return userService.findById(userId);
    }

    //    @PreAuthorize("hasAuthority('todo.update') OR "
//        + "(hasAuthority('user.todo.update') AND @customAuthenticationManager.userIdMatches(authentication, #userId))")
    @Operation(summary = "특정 유저 email수정", description = "특정 유저 email 수정")
    @PatchMapping("/users/{userId}/email")
    public UserOnlyIdResponse getUserById(@PathVariable String userId,
        @Validated @Parameter @RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.updateEmail(userId, userUpdateRequest);
    }



}
