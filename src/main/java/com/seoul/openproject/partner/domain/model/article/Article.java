package com.seoul.openproject.partner.domain.model.article;

import com.seoul.openproject.partner.domain.ArticleMatchCondition;
import com.seoul.openproject.partner.domain.model.ArticleMember;
import com.seoul.openproject.partner.domain.model.BaseTimeVersionEntity;
import com.seoul.openproject.partner.domain.model.match.MatchCondition;
import com.seoul.openproject.partner.domain.model.member.Member;
import com.seoul.openproject.partner.domain.model.member.Member.MemberDto;
import com.seoul.openproject.partner.dto.ListResponse;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class Article extends BaseTimeVersionEntity {
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
    private String title;

    //longtext형
    @Lob
    @Column(nullable = false)
    private String content;


    @Builder.Default
    @Column(nullable = false)
    private Boolean complete = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer participantNum = 1;

    @Column(nullable = false)
    private Integer participantNumMax ;







    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/



    /*********************************  *********************************/

    @Builder.Default
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<ArticleMatchCondition> articleMatchConditions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<ArticleMember> articleMembers = new ArrayList<>();

    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

    public static Article of(String title, String content, Integer participantNumMax, ArticleMember articleMember, List<ArticleMatchCondition> articleMatchConditions) {
        Article article = Article.builder()
            .title(title)
            .content(content)
            .participantNumMax(participantNumMax)
            .build();
        articleMember.setArticle(article);
        for (ArticleMatchCondition articleMatchCondition : articleMatchConditions) {
            articleMatchCondition.setArticle(article);
        }
        return article;
    }
    /********************************* 비니지스 로직 *********************************/


    public void update(String title, String content, Integer pariticipantNumMax , List<ArticleMatchCondition> articleMatchConditions){
        this.title = title;
        this.content = content;
        this.participantNumMax = pariticipantNumMax;
        for (ArticleMatchCondition articleMatchCondition : articleMatchConditions) {
            articleMatchCondition.setArticle(this);
        }
    }

    public Member getAuthorMember(){
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
    public static class ArticleDto{
        @Schema(name= "memberId" , example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "작성자 ID")
        @NotBlank
        @Size(min = 1, max = 100)
        private String memberId;

        @Schema(name= "title" , example = "개포에서 2시에 점심 먹으실 분 구합니다.", description = "글 제목")
        @NotBlank
        @Size(min = 1, max = 255)
        private String title;

        @Schema(name= "place" , example = "SEOCHO(서초 클러스터), GAEPO(개포 클러스터), OUT_OF_CLUSTER(클러스터 외부)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<Place> place ;
        @Schema(name= "timeOfEating" , example = "BREAKFAST(아침 식사), LUNCH(점심 식사), DUNCH(점저), DINNER(저녁 식사), MIDNIGHT(야식)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<TimeOfEating> timeOfEating;
        @Schema(name= "typeOfEating" , example = " KOREAN(한식), JAPANESE(일식), CHINESE(중식),"
            + "    WESTERN(양식), ASIAN(아시안), EXOTIC(이국적인), CONVENIENCE(편의점)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<TypeOfEating> typeOfEating ;
        @Schema(name= "wayOfEating" , example = " DELIVERY(배달), EATOUT(외식), TAKEOUT(포장)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<WayOfEating> wayOfEating ;

        @Schema(name= "content" , example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "글 본문")
        @NotNull
        @Size(min = 1, max = 100000)
        private String content;

        @Schema(name= "authorAnonymity" , example = "true", description = "익명 여부")
        @NotNull
        private Boolean authorAnonymity;

        @Schema(name= "participantNumMAx" , example = "5", description = "방 최대 참여자 수")
        @NotNull
        @Min(1)
        @Max(20)
        private Integer participantNumMax;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ArticleReadOneResponse{


        @Schema(name="articleId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "게시글 ID")
        @NotBlank
        @Size(min = 1, max = 100)
        private String articleId;

        @Schema(name= "title" , example = "개포에서 2시에 점심 먹으실 분 구합니다.", description = "글 제목")
        @NotBlank
        @Size(min = 1, max = 255)
        private String title;

        @Schema(name= "content" , example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "글 본문")
        @NotNull
        @Size(min = 1, max = 100000)
        private String content;

        @Schema(name= "participantNumMax" , example = "5", description = "방 최대 참여자 수")
        @NotNull
        @Min(1)
        @Max(20)
        private Integer participantNumMax;

        @Schema(name= "participantNum" , example = " 2", description = "현재 방에 참여중인 인원")
        @NotNull
        private Integer participantNum;

//        @Schema(name= "matchConditions" , example = " ", description = "Json 객체 배열")
//        @NotNull
//        private ListResponse<MatchCondition.MatchConditionDto> matchConditions;

//        @Schema(name= "matchConditionCount" , example = "4", description = "matchConditions 의 개수.")
//        @NotNull
//        private Integer matchConditionCount;

        @Builder.Default
        @Schema(name= "matchConditions" , example = " ", description = "매치 조건 배열")
        private List<MatchCondition.MatchConditionDto> matchConditions = new ArrayList<>();

//        @Schema(name= "participantsOrAuthor" , example = " ", description = "Json 객체 배열")
//        @NotNull
//        private ListResponse<Member.MemberDto> participantsOrAuthor;
//
//        @Schema(name= "participantsOrAuthorCount" , example = "4", description = "participantsOrAuthor 의 개수.")
//        @NotNull
//        private Integer participantsOrAuthorCount;



        @Builder.Default
        @Schema(name= "participantsOrAuthor" , example = " ", description = "방을 만든사람, 혹은 참여자가 담긴 배열")
        private List<Member.MemberDto> participantsOrAuthor = new ArrayList<>();

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ArticleReadResponse{


        @Schema(name="articleId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "게시글 ID")
        @NotBlank
        @Size(min = 1, max = 100)
        private String articleId;

        @Schema(name= "title" , example = "개포에서 2시에 점심 먹으실 분 구합니다.", description = "글 제목 20줄까지만 보내짐.")
        @NotBlank
        @Size(min = 1, max = 255)
        private String title;

        @Schema(name= "content" , example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "글 본문 글 제목 20줄까지만 보내짐.")
        @NotNull
        @Size(min = 1, max = 100000)
        private String content;

        @Schema(name= "participantNumMax" , example = "5", description = "방 최대 참여자 수")
        @NotNull
        @Min(1)
        @Max(20)
        private Integer participantNumMax;

        @Schema(name= "participantNum" , example = " 2", description = "현재 방에 참여중인 인원")
        @NotNull
        private Integer participantNum;
        @Builder.Default
        @Schema(name= "matchConditions" , example = " ", description = "매치 조건 배열")
        private List<MatchCondition.MatchConditionDto> matchConditions = new ArrayList<>();


    }


}
