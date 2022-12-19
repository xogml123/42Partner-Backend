package partner42.moduleapi.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
public class OAuthController {

    @Operation(summary = "oauth login", description = "호출하면 자동 로그인후 리다이렉션")
    @GetMapping("/oauth2/authorization/authclient")
    public void fakeLogin() {
        throw new IllegalStateException(
            "This method shouldn't be called. It's implemented by Spring Security filters.");

    }
}
