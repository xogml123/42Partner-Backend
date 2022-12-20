package partner42.moduleapi.controller.article;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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
import partner42.moduleapi.dto.EmailDto;
import partner42.moduleapi.dto.article.ArticleDto;
import partner42.moduleapi.dto.article.ArticleOnlyIdResponse;
import partner42.moduleapi.dto.article.ArticleReadOneResponse;
import partner42.moduleapi.dto.article.ArticleReadResponse;
import partner42.moduleapi.service.article.ArticleService;
import partner42.modulecommon.repository.article.ArticleSearch;
import partner42.modulecommon.utils.slack.SlackBotService;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final SlackBotService slackBotService;

    @Operation(summary = "방 하나 상세조회", description = "방 상세페이지")
    @GetMapping("/articles/{articleId}")
    public ArticleReadOneResponse readOneArticle(
        @PathVariable String articleId,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user) {


        String username = user != null ? user.getUsername() : null;
        return articleService.readOneArticle(username, articleId);
    }

//    @PreAuthorize("hasAuthority('article.read')")
    @Operation(summary = "방 목록조회", description = "방 목록 페이지, ")
    @GetMapping("/articles")
    public SliceImpl<ArticleReadResponse> readAllArticle(Pageable pageable, ArticleSearch condition) {
        return articleService.readAllArticle(pageable, condition);
    }

    @PreAuthorize("hasAuthority('article.create')")
    @Operation(summary = "방 매칭 글쓰기", description = "방 매칭 글쓰기")
    @PostMapping("/articles")
    public ArticleOnlyIdResponse writeArticle(
        @ApiParam(hidden = true) @AuthenticationPrincipal UserDetails user,
        @Validated @Parameter @RequestBody ArticleDto articleRequest) {
        return articleService.createArticle(user.getUsername(), articleRequest);
    }

    @PreAuthorize("hasAuthority('article.update')")
    @Operation(summary = "방 매칭 글수정", description = "방 매칭 글 수정")
    @PutMapping("/articles/{articleId}")
    public ArticleOnlyIdResponse updateArticle(@Validated @Parameter @RequestBody ArticleDto articleRequest,
        @PathVariable String articleId,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user) {
        return articleService.updateArticle(articleRequest, user.getUsername(), articleId);
    }

    @PreAuthorize("hasAuthority('article.delete')")
    @Operation(summary = "방 매칭 삭제", description = "방 매칭 삭제")
    @DeleteMapping("/articles/{articleId}")
    public ArticleOnlyIdResponse deleteArticle(@PathVariable String articleId,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user) {
        return articleService.deleteArticle(user.getUsername(), articleId);
    }
    @PreAuthorize("hasAuthority('article.update')")
    @Operation(summary = "방 매칭 글 임시 삭제", description = "방 매칭 글 임시 삭제")
    @PostMapping("/articles/{articleId}/recoverable-delete")
    public ArticleOnlyIdResponse recoverableDeleteArticle(@PathVariable String articleId,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user
    ) {
        return articleService.changeIsDelete(user.getUsername(), articleId);
    }

    @PreAuthorize("hasAuthority('article.update')")
    @Operation(summary = "방 매칭 참여", description = "방 매칭 참여")
    @PostMapping("/articles/{articleId}/participate")
    public ArticleOnlyIdResponse participateArticle(@PathVariable String articleId,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user
    ) {
        return articleService.participateArticle(user.getUsername(), articleId);
    }

    @PreAuthorize("hasAuthority('article.update')")
    @Operation(summary = "방 매칭 참여 최소", description = "방 매칭 참여 최소")
    @PostMapping("/articles/{articleId}/participate-cancel")
    public ArticleOnlyIdResponse participateCancelArticle(@PathVariable String articleId,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user
    ) {
        return articleService.participateCancelArticle(user.getUsername(), articleId);
    }

    @PreAuthorize("hasAuthority('article.update')")
    //작성자인지 확인하는 권한 처리.
    @Operation(summary = "방 매칭 글 확정", description = "방 매칭 글 확정")
    @PostMapping("/articles/{articleId}/complete")
    public ArticleOnlyIdResponse completeArticle(@PathVariable String articleId,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user
    ) {
        EmailDto<ArticleOnlyIdResponse> emailDto = articleService.completeArticle(
            user.getUsername(), articleId);
        //트랜잭션 외부에서 외부 리소스 알림기능을 적용하기 위해서
        //따로 분리.
        List<String> participantsEmails = emailDto.getEmails();
        slackBotService.createSlackMIIM(participantsEmails);
        return emailDto.getResponse();
    }

}
