package partner42.moduleapi.controller.opinion;


import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import partner42.moduleapi.dto.alarm.ResponseWithAlarmEventDto;
import partner42.moduleapi.dto.opinion.OpinionDto;
import partner42.moduleapi.dto.opinion.OpinionOnlyIdResponse;
import partner42.moduleapi.dto.opinion.OpinionResponse;
import partner42.moduleapi.dto.opinion.OpinionUpdateRequest;
import partner42.moduleapi.service.opinion.OpinionService;
import partner42.modulecommon.producer.alarm.AlarmProducer;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OpinionController {

    private final OpinionService opinionService;
    private final AlarmProducer alarmProducer;

    @PreAuthorize("hasAuthority('opinion.create')")
    @Operation(summary = "댓글 생성", description = "댓글 생성")
    @PostMapping("/opinions")
    public OpinionOnlyIdResponse createOpinion(@Validated @Parameter @RequestBody OpinionDto request,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user) {
        ResponseWithAlarmEventDto<OpinionOnlyIdResponse> dto = opinionService.createOpinion(
            request, user.getUsername());
        if (dto.getAlarmEvent() != null){
            alarmProducer.send(dto.getAlarmEvent());
        }
        return dto.getResponse();
    }
    @PreAuthorize("hasAuthority('opinion.update')")
    @Operation(summary = "댓글 수정", description = "댓글 수정")
    @PutMapping("/opinions/{opinionId}")
    public OpinionOnlyIdResponse updateOpinion(@Validated @Parameter @RequestBody OpinionUpdateRequest request,
        @PathVariable String opinionId,
        @ApiParam(hidden = true) @AuthenticationPrincipal UserDetails user) {
        return opinionService.updateOpinion(request, opinionId, user.getUsername());
    }

    @PreAuthorize("hasAuthority('opinion.update')")
    @Operation(summary = "댓글 임시 삭제", description = "댓글 임시 삭제")
    @PostMapping("/opinions/{opinionId}/recoverable-delete")
    public OpinionOnlyIdResponse recoverableDeleteOpinion(
        @PathVariable String opinionId,
        @ApiParam(hidden = true) @AuthenticationPrincipal UserDetails user
        ) {
        return opinionService.recoverableDeleteOpinion(opinionId, user.getUsername());
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

    @PreAuthorize("hasAuthority('opinion.delete')")
    @Operation(summary = "댓글 완전 삭제", description = "댓글 완전 삭제 (관리자 전용)")
    @DeleteMapping("/opinions/{opinionId}")
    public OpinionOnlyIdResponse completeDeleteOpinion(
        @PathVariable String opinionId,
        @ApiParam(hidden = true) @AuthenticationPrincipal UserDetails user) {
        return opinionService.completeDeleteOpinion(opinionId, user.getUsername());
    }

}
