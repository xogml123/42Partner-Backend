package partner42.moduleapi.controller.match;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import partner42.moduleapi.dto.article.ArticleDto;
import partner42.moduleapi.dto.match.MatchDto;
import partner42.moduleapi.dto.match.MatchReviewRequest;
import partner42.moduleapi.service.match.MatchService;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.repository.match.MatchSearch;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PreAuthorize("hasAuthority('match.read')")
    @Operation(summary = "자신의 매치이력 조회", description = "자신의 매치이력 조회")
    @GetMapping("/matches")
    public SliceImpl<MatchDto> readMyMatches(
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user,
        MatchSearch matchSearch,
        Pageable pageable
    ) {
        return matchService.readMyMatches(user.getUsername(), matchSearch, pageable);
    }

    @PreAuthorize("hasAuthority('match.read')")
    @Operation(summary = "특정 매치이력 조회", description = "특정 매치이력 조회")
    @GetMapping("/matches/{matchId}")
    public MatchDto readOneMatch(
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user,
        @PathVariable("matchId") String matchId
    ) {
        return matchService.readOneMatch(user.getUsername(), matchId);
    }

    @PreAuthorize("hasAuthority('match.update') and hasAuthority('activity.create')")
    @Operation(summary = "매칭 후기 쓰기", description = "특정 매치이력 조회")
    @PostMapping("/matches/{matchId}/review")
    public ResponseEntity<Void> makeReview(
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user,
        @PathVariable("matchId") String matchId,
        @Validated @Parameter @RequestBody MatchReviewRequest request
    ) {
        matchService.makeReview(user.getUsername(), matchId, request);
        return ResponseEntity.ok().build();

    }


}