package partner42.moduleapi.controller.random;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import partner42.moduleapi.dto.random.RandomMatchDto;
import partner42.moduleapi.service.RandomMatchService;
import partner42.modulecommon.domain.model.user.User;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RandomMatchController {

    private final RandomMatchService randomMatchService;

    @Operation(summary = "랜덤 매칭 신청", description = "랜덤 매칭 신청")
    @PostMapping("/random-matches")
    public ResponseEntity<Void> applyRandomMatch(
        @ApiParam(hidden = true) @AuthenticationPrincipal User user,
        @Validated @Parameter @RequestBody RandomMatchDto randomMatchDto) {
        //contentCategory에 따라 필드 검증
        return randomMatchService.createRandomMatch(user.getApiId(), randomMatchDto);
    }

    @Operation(summary = "랜덤 매칭 취소", description = "랜덤 매칭 취소")
    @DeleteMapping("/random-matches/mine")
    public ResponseEntity<Void> cancelRandomMatch(
        @ApiParam(hidden = true) @AuthenticationPrincipal User user) {
        //contentCategory에 따라 필드 검증
        return randomMatchService.deleteRandomMatch(user.getApiId());
    }
}
