package partner42.moduleapi.controller.match;

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
import partner42.moduleapi.dto.match.MatchDto;
import partner42.moduleapi.service.match.MatchService;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.repository.match.MatchSearch;

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