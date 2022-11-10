package com.seoul.openproject.partner.controller.activity;


import com.seoul.openproject.partner.domain.model.activity.Activity;
import com.seoul.openproject.partner.domain.model.user.User;
import com.seoul.openproject.partner.repository.activity.ActivitySearch;
import com.seoul.openproject.partner.service.activity.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @Operation(summary = "자신의 활동점수 조회", description = "자신의 활동 점수 조회 월/년단위로 조회 / 밥, 공부여부 지정")
    @GetMapping("/activities/score")
    public Activity.ActivityScoreResponse readMyActivityScore(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        ActivitySearch activitySearch
        ) {
        return activityService.readMyActivityScoreSum(user.getApiId(), activitySearch);
    }
}
