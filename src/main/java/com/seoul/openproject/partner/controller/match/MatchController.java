package com.seoul.openproject.partner.controller.match;

import com.seoul.openproject.partner.domain.model.match.Match;
import com.seoul.openproject.partner.domain.model.match.Match.MatchDto;
import com.seoul.openproject.partner.domain.model.user.User;
import com.seoul.openproject.partner.repository.match.MatchSearch;
import com.seoul.openproject.partner.service.match.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @Operation(summary = "자신의 매치이력 조회", description = "자신의 매치이력 조회 ")
    @GetMapping("/matches")
    public Slice<MatchDto> readMyMatches(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        MatchSearch matchSearch,
        Pageable pageable
    ) {
        return matchService.readMyMatches(user.getApiId(), matchSearch, pageable);
    }
}