package com.seoul.openproject.partner.domain.model.opnion;

import com.seoul.openproject.partner.domain.model.BaseEntity;
import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "OPINION", uniqueConstraints = {
    @UniqueConstraint(name = "API_ID_UNIQUE", columnNames = {"apiId"})
})
@Entity
public class Opinion extends BaseEntity {
    //********************************* static final 상수 필드 *********************************/

    /**
     * email 뒤에 붙는 문자열
     */


    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPINION_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/

    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();


    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private String parentApiId;

    @Column(nullable = false, updatable = false)
    private Integer level;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_AUTHOR_ID", nullable = false, updatable = false)
    private Member memberAuthor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID", nullable = false, updatable = false)
    private Article article;


    /********************************* 연관관계 편의 메서드 *********************************/
    public void setMemberAuthorAndArticle(Member memberAuthor, Article article) {
        this.memberAuthor = memberAuthor;
        this.article = article;
        article.getOpinions().add(this);
    }

    /********************************* 생성 메서드 *********************************/

    public static Opinion of(String content, Member memberAuthor, Article article, String parentApiId, Integer level) {
        Opinion opinion = Opinion.builder()
            .content(content)
            .memberAuthor(memberAuthor)
            .parentApiId(parentApiId)
            .level(level)
            .build();
        opinion.setMemberAuthorAndArticle(memberAuthor, article);
        return opinion;
    }



    /********************************* 비니지스 로직 *********************************/

    public void updateContent(String content) {
        this.content = content;
    }

    public void delete() {
        this.isDeleted = true;
    }

    /********************************* DTO *********************************/

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class OpinionOnlyIdResponse {
        @Schema(name = "opinionId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "댓글 ID")
        @NotBlank
        private String opinionId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class OpinionDto{

        @Schema(name= "articleId" , example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "댓글을 단 글의 id")
        @NotBlank
        private String articleId;

        @Schema(name= "content" , example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "댓글 본문")
        @NotNull
        @Size(min = 1, max = 1000)
        private String content;

        @Schema(name= "parentId" , example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "부모 댓글의 id")
        @NotBlank
        private String parentId;

        @Schema(name= "level" , example = "1", description = "첫 댓글이 1이고 대댓글이 2")
        @NotNull
        private Integer level;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class OpinionUpdateRequest{

        @Schema(name= "content" , example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "댓글 본문")
        @NotNull
        @Size(min = 1, max = 1000)
        private String content;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class OpinionResponse {

        @Schema(name= "opinionId" , example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "댓글 id")
        @NotBlank
        private String opinionId;

        @Schema(name= "nickname" , example = "takim", description = "작성자 이름")
        @NotBlank
        private String nickname;

        @Schema(name= "createdAt" , example = "", description = "작성 시간")
        private LocalDateTime createdAt;

        @Schema(name= "updatedAt" , example = "", description = "수정 시간")
        private LocalDateTime updatedAt;

        @Schema(name= "content" , example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "댓글 본문")
        @NotNull
        @Size(min = 1, max = 1000)
        private String content;

        @Schema(name= "parentId" , example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "부모 댓글의 id")
        @NotBlank
        private String parentId;

        @Schema(name= "level" , example = "1 or 2", description = "첫 댓글이 1이고 대댓글이 2")
        @NotNull
        private Integer level;
    }
}


