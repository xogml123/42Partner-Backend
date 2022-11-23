package partner42.moduleapi.controller.activity;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import partner42.moduleapi.dto.activity.ActivityScoreResponse;
import partner42.moduleapi.service.activity.ActivityService;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.repository.activity.ActivitySearch;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @Operation(summary = "자신의 활동점수 조회", description = "자신의 활동 점수 조회 월/년단위로 조회 / 밥, 공부여부 지정")
    @GetMapping("/activities/score")
    public ActivityScoreResponse readMyActivityScore(
        @Parameter(hidden = true) @AuthenticationPrincipal String username,
        ActivitySearch activitySearch
        ) {
        return activityService.readMyActivityScoreSum(username, activitySearch);
    }
}
