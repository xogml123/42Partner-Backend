package partner42.moduleapi.controller.opinion;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import partner42.moduleapi.dto.ListResponse;
import partner42.moduleapi.dto.opinion.OpinionDto;
import partner42.moduleapi.dto.opinion.OpinionOnlyIdResponse;
import partner42.moduleapi.dto.opinion.OpinionResponse;
import partner42.moduleapi.dto.opinion.OpinionUpdateRequest;
import partner42.moduleapi.service.opinion.OpinionService;
import partner42.modulecommon.domain.model.user.User;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OpinionController {

    private final OpinionService opinionService;

    @Operation(summary = "댓글 생성", description = "댓글 생성")
    @PostMapping("/opinions")
    public OpinionOnlyIdResponse createOpinion(@Validated @Parameter @RequestBody OpinionDto request,
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return opinionService.createOpinion(request, user.getApiId());
    }

    @Operation(summary = "댓글 수정", description = "댓글 수정")
    @PutMapping("/opinions/{opinionId}")
    public OpinionOnlyIdResponse updateOpinion(@Validated @Parameter @RequestBody OpinionUpdateRequest request,
        @PathVariable String opinionId) {
        return opinionService.updateOpinion(request, opinionId);
    }

    @Operation(summary = "댓글 임시 삭제", description = "댓글 임시 삭제")
    @PostMapping("/opinions/{opinionId}/recoverable-delete")
    public OpinionOnlyIdResponse recoverableDeleteOpinion(
        @PathVariable String opinionId) {
        return opinionService.recoverableDeleteOpinion(opinionId);
    }

    @Operation(summary = "댓글 전체 조회", description = "댓글 전체 조회")
    @GetMapping("/articles/{articleId}/opinions")
    public ListResponse<OpinionResponse> getAllOpinionsInArticle(@PathVariable String articleId) {
        return opinionService.findAllOpinionsInArticle(articleId);
    }

    @Operation(summary = "댓글 하나 조회", description = "댓글 하나 조회")
    @GetMapping("/opinions/{opinionId}")
    public OpinionResponse getOneOpinion(
        @PathVariable String opinionId) {
        return opinionService.getOneOpinion(opinionId);
    }

    @Operation(summary = "댓글 완전 삭제", description = "댓글 완전 삭제 (관리자 전용)")
    @DeleteMapping("/opinions/{opinionId}")
    public OpinionOnlyIdResponse completeDeleteOpinion(
        @PathVariable String opinionId) {
        return opinionService.completeDeleteOpinion(opinionId);
    }

}
