package com.seoul.openproject.partner.domain.model.article;

import com.seoul.openproject.partner.domain.model.matchcondition.ArticleMatchCondition;
import com.seoul.openproject.partner.domain.model.BaseEntity;
import com.seoul.openproject.partner.domain.model.match.MatchCondition;
import com.seoul.openproject.partner.domain.model.member.Member;
import com.seoul.openproject.partner.domain.model.opnion.Opinion;
import com.seoul.openproject.partner.mapper.MatchConditionMapper;
import com.seoul.openproject.partner.mapper.MemberMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
@Table(name = "ARTICLE", uniqueConstraints = {
    @UniqueConstraint(name = "API_ID_UNIQUE", columnNames = {"apiId"}),
})
@Entity
public class Article extends BaseEntity {
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
    @Column(name = "ARTICLE_ID")
    private Long id;

    /********************************* PK가 아닌 필드 *********************************/

    /**
     * AUTH에 필요한 필드
     */

    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String title;

    //longtext형
    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean anonymity;

    @Builder.Default
    @Column(nullable = false)
    private Boolean complete = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer participantNum = 1;

    @Column(nullable = false)
    private Integer participantNumMax;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/


    /*********************************  *********************************/

    @Builder.Default
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE,
        CascadeType.PERSIST})
    private List<ArticleMatchCondition> articleMatchConditions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<ArticleMember> articleMembers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Opinion> Opinions = new ArrayList<>();

    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

    public static Article of(LocalDate date, String title, String content, Boolean anonymity,
        Integer participantNumMax, ArticleMember articleMember,
        List<ArticleMatchCondition> articleMatchConditions) {
        Article article = Article.builder()
            .date(date)
            .title(title)
            .content(content)
            .anonymity(anonymity)
            .participantNumMax(participantNumMax)
            .build();
        articleMember.setArticle(article);
        for (ArticleMatchCondition articleMatchCondition : articleMatchConditions) {
            articleMatchCondition.setArticle(article);
        }
        return article;
    }

    /********************************* 비니지스 로직 *********************************/


    public void update(LocalDate date, String title, String content, Integer participantNumMax,
        List<ArticleMatchCondition> articleMatchConditions) {
        this.date = date;
        this.title = title;
        this.content = content;
        this.participantNumMax = participantNumMax;
        for (ArticleMatchCondition articleMatchCondition : articleMatchConditions) {
            articleMatchCondition.setArticle(this);
        }
    }

    public Member getAuthorMember() {
        return this.getArticleMembers().stream()
            .filter((articleMember) ->
                articleMember.getIsAuthor())
            .map((articleMember) ->
                articleMember.getMember())
            .findFirst().orElseThrow(() -> (
                new EntityNotFoundException("해당 게시글의 작성자가 존재하지 않습니다.")
            ));
    }

    public List<Member> getParticipatedMembers() {
        return this.getArticleMembers().stream()
            .filter((articleMember) ->
                !articleMember.getIsAuthor())
            .map((articleMember) ->
                articleMember.getMember())
            .collect(Collectors.toList());
    }

    public boolean isParticipantNumMaxChangeable(Integer participantNumMax) {
        return this.participantNum <= participantNumMax;
    }

    public boolean isDateToday() {
        return this.date.isEqual(LocalDate.now());

    }

    public ArticleMember participate(Member member) {
        if (this.participantNumMax <= this.participantNum) {
            throw new IllegalArgumentException("모집인원이 초과되었습니다.");
        }
        if (this.getArticleMembers().stream()
            .anyMatch((articleMember) ->
                articleMember.getMember().equals(member))) {
            throw new IllegalArgumentException("이미 참여한 게시글입니다.");
        }
        ArticleMember articleMember = ArticleMember.of(member, false);
        articleMember.setArticle(this);
        this.participantNum++;
        return articleMember;
    }

    public ArticleMember participateCancel(Member member) {
        ArticleMember articleMember = this.getArticleMembers().stream()
            .filter((articleMember1) ->
                articleMember1.getMember().getApiId().equals(member.getApiId()))
            .findFirst().orElseThrow(() -> (
                new EntityNotFoundException("해당 게시글의 참여자가 존재하지 않습니다.")
            ));
        this.participantNum--;
        return articleMember;
    }

    public void complete() {
        this.complete = true;
    }

    /********************************* DTO *********************************/
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ArticleOnlyIdResponse {

        @Schema(name = "articleId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "게시글 ID")
        private String articleId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ArticleDto {

        @Schema(name = "memberId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "작성자 ID")
        @NotBlank
        @Size(min = 1, max = 100)
        private String memberId;

        @Schema(name = "title", example = "개포에서 2시에 점심 먹으실 분 구합니다.", description = "글 제목")
        @NotBlank
        @Size(min = 1, max = 255)
        private String title;

        @Schema(name = "date", example = "2022-10-03", description = "식사 날짜")
        @NotNull
        private LocalDate date;

        @Schema(name = "place", example = "SEOCHO(서초 클러스터), GAEPO(개포 클러스터), OUT_OF_CLUSTER(클러스터 외부)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<Place> place;
        @Schema(name = "timeOfEating", example = "BREAKFAST(아침 식사), LUNCH(점심 식사), DUNCH(점저), DINNER(저녁 식사), MIDNIGHT(야식)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<TimeOfEating> timeOfEating;

        @Schema(name = "wayOfEating", example = " DELIVERY(배달), EATOUT(외식), TAKEOUT(포장)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<WayOfEating> wayOfEating;

        @Schema(name = "content", example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "글 본문")
        @NotNull
        @Size(min = 1, max = 100000)
        private String content;

        @Schema(name = "anonymity", example = "true", description = "익명 여부")
        @NotNull
        private Boolean anonymity;

        @Schema(name = "participantNumMAx", example = "5", description = "방 최대 참여자 수")
        @NotNull
        @Min(1)
        @Max(20)
        private Integer participantNumMax;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ArticleUpdateRequest {

        @Schema(name = "title", example = "개포에서 2시에 점심 먹으실 분 구합니다.", description = "글 제목")
        @NotBlank
        @Size(min = 1, max = 255)
        private String title;

        @Schema(name = "date", example = "2022-10-03", description = "식사 날짜")
        @NotNull
        private LocalDate date;

        @Schema(name = "place", example = "SEOCHO(서초 클러스터), GAEPO(개포 클러스터), OUT_OF_CLUSTER(클러스터 외부)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<Place> place;
        @Schema(name = "timeOfEating", example = "BREAKFAST(아침 식사), LUNCH(점심 식사), DUNCH(점저), DINNER(저녁 식사), MIDNIGHT(야식)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<TimeOfEating> timeOfEating;

        @Schema(name = "wayOfEating", example = " DELIVERY(배달), EATOUT(외식), TAKEOUT(포장)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<WayOfEating> wayOfEating;

        @Schema(name = "content", example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "글 본문")
        @NotNull
        @Size(min = 1, max = 100000)
        private String content;


        @Schema(name = "participantNumMAx", example = "5", description = "방 최대 참여자 수")
        @NotNull
        @Min(1)
        @Max(20)
        private Integer participantNumMax;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ArticleReadOneResponse {


        @Schema(name = "articleId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "게시글 ID")
        @NotBlank
        @Size(min = 1, max = 100)
        private String articleId;

        @Schema(name = "title", example = "개포에서 2시에 점심 먹으실 분 구합니다.", description = "글 제목")
        @NotBlank
        @Size(min = 1, max = 255)
        private String title;

        @Schema(name = "date", example = "2022-10-03", description = "식사 날짜")
        @NotNull
        private LocalDate date;

        @Schema(name = "createdAt", example = "", description = "작성 시간")
        private LocalDateTime createdAt;


        @Schema(name = "content", example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "글 본문")
        @NotNull
        @Size(min = 1, max = 100000)
        private String content;

        @Schema(name = "anonymity", example = "true", description = "익명 여부")
        @NotNull
        private Boolean anonymity;

        @Schema(name = "isToday", example = "true", description = "당일 여부")
        @NotNull
        private Boolean isToday;

        @Schema(name = "participantNumMax", example = "5", description = "방 최대 참여자 수")
        @NotNull
        @Min(1)
        @Max(20)
        private Integer participantNumMax;

        @Schema(name = "participantNum", example = " 2", description = "현재 방에 참여중인 인원")
        @NotNull
        private Integer participantNum;


        @Builder.Default
        @Schema(name = "matchConditions", example = " ", description = "매치 조건 배열")
        private List<MatchCondition.MatchConditionDto> matchConditions = new ArrayList<>();


        @Builder.Default
        @Schema(name = "participantsOrAuthor", example = " ", description = "방을 만든사람, 혹은 참여자가 담긴 배열")
        private List<Member.MemberDto> participantsOrAuthor = new ArrayList<>();

        public static ArticleReadOneResponse of(Article article, MemberMapper memberMapper,
            MatchConditionMapper matchConditionMapper) {
            return ArticleReadOneResponse.builder()
                .articleId(article.getApiId())
                .title(article.getTitle())
                .date(article.getDate())
                .createdAt(article.getCreatedAt())
                .content(article.getContent())
                .anonymity(article.getAnonymity())
                .isToday(article.isDateToday())
                .participantNumMax(article.getParticipantNumMax())
                .participantNum(article.getParticipantNum())
                .participantsOrAuthor(article.getArticleMembers().stream()
                    .map(am -> (
                        memberMapper.entityToMemberDto(am.getMember(), am)))
                    .collect(Collectors.toList()))
                .matchConditions(article.getArticleMatchConditions().stream()
                    .map(arc -> (
                        matchConditionMapper.entityToMatchConditionDto(arc.getMatchCondition())
                    ))
                    .collect(Collectors.toList()))
                .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ArticleReadResponse {

        @Schema(name = "nickname", example = "takim", description = "게시글 작성자 nickname")
        @NotBlank
        @Size(min = 1, max = 100)
        private String nickname;

        @Schema(name = "articleId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "게시글 ID")
        @NotBlank
        @Size(min = 1, max = 100)
        private String articleId;

        @Schema(name = "title", example = "개포에서 2시에 점심 먹으실 분 구합니다.", description = "글 제목 20줄까지만 보내짐.")
        @NotBlank
        @Size(min = 1, max = 255)
        private String title;

        @Schema(name = "content", example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "글 본문 글 제목 20줄까지만 보내짐.")
        @NotNull
        @Size(min = 1, max = 100000)
        private String content;


        @Schema(name = "date", example = "2022-10-03", description = "식사 날짜")
        @NotNull
        private LocalDate date;

        @Schema(name = "createdAt", example = "", description = "작성 시간")
        private LocalDateTime createdAt;

        @Schema(name = "anonymity", example = "true", description = "익명 여부")
        @NotNull
        private Boolean anonymity;

        @Schema(name = "isToday", example = "true", description = "당일 여부")
        @NotNull
        private Boolean isToday;

        @Schema(name = "participantNumMax", example = "5", description = "방 최대 참여자 수")
        @NotNull
        @Min(1)
        @Max(20)
        private Integer participantNumMax;

        @Schema(name = "participantNum", example = " 2", description = "현재 방에 참여중인 인원")
        @NotNull
        private Integer participantNum;

        @Builder.Default
        @Schema(name = "matchConditions", example = " ", description = "매치 조건 배열")
        private List<MatchCondition.MatchConditionDto> matchConditions = new ArrayList<>();

        public static ArticleReadResponse of(Article article,
            MatchConditionMapper matchConditionMapper) {
            return ArticleReadResponse.builder()
                .nickname(article.articleMembers.stream()
                    .filter(a ->
                        a.getIsAuthor())
                    .findAny()
                    .orElseThrow(() ->
                        new EntityNotFoundException("해당 게시글에 작성자가 없습니다."))
                    .getMember().getNickname())
                .articleId(article.getApiId())
                .title(article.getTitle())
                .content(article.getContent())
                .date(article.getDate())
                .createdAt(article.getCreatedAt())
                .anonymity(article.getAnonymity())
                .isToday(article.getDate().isEqual(LocalDate.now()))
                .participantNumMax(article.getParticipantNumMax())
                .participantNum(article.getParticipantNum())
                .matchConditions(article.getArticleMatchConditions().stream()
                    .map(arc -> (
                        matchConditionMapper.entityToMatchConditionDto(arc.getMatchCondition())
                    ))
                    .collect(Collectors.toList()))
                .build();
        }

    }


}
