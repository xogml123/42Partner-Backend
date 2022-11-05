package com.seoul.openproject.partner.controller.article;

import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.article.Article.ArticleReadResponse;
import com.seoul.openproject.partner.domain.model.user.User;
import com.seoul.openproject.partner.dto.ListResponse;
import com.seoul.openproject.partner.error.ErrorResult;
import com.seoul.openproject.partner.repository.article.ArticleSearch;
import com.seoul.openproject.partner.service.article.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArticleController {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResult> entityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResult.builder()
                .message(e.getMessage())
                .build());
    }

    //데이터 정합성에 문제가 있는경우 명백한 서버 에러
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResult> illegalStateException(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResult.builder()
                .message(e.getMessage())
                .build());
    }
    //요청의 처리가 불가능한 경우 -> 방 참여자수가 이미 다 차잇는데 참여를 누르는 경우.

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResult> illegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResult.builder()
                .message(e.getMessage())
                .build());
    }


    private final ArticleService articleService;


    @Operation(summary = "방 하나 상세조회", description = "방 상세페이지")
    @GetMapping("/articles/{articleId}")
    public Article.ArticleReadOneResponse readOneArticle(
        @PathVariable String articleId) {
        return articleService.readOneArticle(articleId);
    }

    @Operation(summary = "방 목록조회", description = "방 목록 페이지, ")
    @GetMapping("/articles")
    public Slice<ArticleReadResponse> readAllArticle(Pageable pageable, ArticleSearch condition) {
        return articleService.readAllArticle(pageable, condition);
    }


//    @PreAuthorize("hasAuthority('article.create') OR "
//        + "(hasAuthority('article.create') AND @customAuthenticationManager.userIdMatches(authentication, #articleRequest))")
    @Operation(summary = "방 매칭 글쓰기", description = "방 매칭 글쓰기")
    @PostMapping("/articles")
    public Article.ArticleOnlyIdResponse writeArticle(@Validated @Parameter @RequestBody Article.ArticleDto articleRequest) {
        return articleService.createArticle(articleRequest);
    }

    @Operation(summary = "방 매칭 글수정", description = "방 매칭 글 수정")
    @PutMapping("/articles/{articleId}")
    public Article.ArticleOnlyIdResponse updateArticle(@Validated @Parameter @RequestBody Article.ArticleDto articleRequest,
        @PathVariable String articleId) {
        return articleService.updateArticle(articleRequest, articleId);
    }

    @Operation(summary = "방 매칭 삭제", description = "방 매칭 삭제")
    @DeleteMapping("/articles/{articleId}")
    public Article.ArticleOnlyIdResponse deleteArticle(@PathVariable String articleId) {
        return articleService.deleteArticle(articleId);
    }

    @Operation(summary = "방 매칭 참여", description = "방 매칭 참여")
    @PostMapping("/articles/{articleId}/participate")
    public Article.ArticleOnlyIdResponse participateArticle(@PathVariable String articleId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        return articleService.participateArticle(user.getApiId(), articleId);
    }

    @Operation(summary = "방 매칭 참여 최소", description = "방 매칭 참여 최소")
    @PostMapping("/articles/{articleId}/participate-cancel")
    public Article.ArticleOnlyIdResponse participateCancelArticle(@PathVariable String articleId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        return articleService.participateCancelArticle(user.getApiId(), articleId);
    }

    //작성자인지 확인하는 권한 처리.
    @Operation(summary = "방 매칭 글 확정", description = "방 매칭 글 확정")
    @PostMapping("/articles/{articleId}/complete")
    public Article.ArticleOnlyIdResponse completeArticle(@PathVariable String articleId,
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        return articleService.completeArticle(user.getApiId(), articleId);
    }

}
