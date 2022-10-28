package com.seoul.openproject.partner.domain.model.article;

import com.seoul.openproject.partner.domain.ArticleMatchCondition;
import com.seoul.openproject.partner.domain.model.BaseTimeVersionEntity;
import com.seoul.openproject.partner.domain.model.MatchConditionMatch;
import com.seoul.openproject.partner.domain.model.match.MatchCondition;
import com.seoul.openproject.partner.domain.model.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
import lombok.Singular;


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
    @Column(name = "MEMBER_ID")
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
    private Boolean anonymity = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean complete = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer participantNum = 0;






    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_AUTHOR_ID", nullable = false, updatable = false)
    private Member memberAuthor;


    /*********************************  *********************************/

    @Builder.Default
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    private List<ArticleMatchCondition> articleMatchConditions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "participatedArticle", fetch = FetchType.LAZY)
    private List<Member> participants = new ArrayList<>();

    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

    public static Article of(String title, String content, boolean anonymity, Member memberAuthor, List<ArticleMatchCondition> articleMatchConditions) {
        Article article = Article.builder()
            .title(title)
            .content(content)
            .anonymity(anonymity)
            .build();
        article.addMemberAuthor(memberAuthor);
        for (ArticleMatchCondition articleMatchCondition : articleMatchConditions) {
            articleMatchCondition.setArticle(article);
        }
        return article;
    }
    /********************************* 비니지스 로직 *********************************/
    public void addMemberAuthor(Member memberAuthor){
        this.memberAuthor = memberAuthor;
        memberAuthor.getWrittenArticles().add(this);
    }

    /********************************* DTO *********************************/
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ArticleOnlyIdResponse {
        @Schema(name = "articleId", description = "게시글 ID")
        private String articleId;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ArticleDto{
        @Schema(name= "memberId" , example = "4f3dda35-3739-406c-ad22-eed438831d66")
        @NotBlank
        @Size(min = 1, max = 100)
        private String memberId;

        @Schema(name= "title" , example = "글 제목")
        @NotBlank
        @Size(min = 1, max = 255)
        private String title;

        @Schema(name= "place" , example = "SEOCHO(서초 클러스터), GAEPO(개포 클러스터), OUT_OF_CLUSTER(클러스터 외부)")
        private List<Place> place ;
        @Schema(name= "timeOfEating" , example = "BREAKFAST(아침 식사), LUNCH(점심 식사), DUNCH(점저), DINNER(저녁 식사), MIDNIGHT(야식)")
        private List<TimeOfEating> timeOfEating;
        @Schema(name= "typeOfEating" , example = " KOREAN(한식), JAPANESE(일식), CHINESE(중식),"
            + "    WESTERN(양식), ASIAN(아시안), EXOTIC(이국적인), CONVENIENCE(편의점)")
        private List<TypeOfEating> typeOfEating ;
        @Schema(name= "wayOfEating" , example = " DELIVERY(배달), EATOUT(외식), TAKEOUT(포장)")
        private List<WayOfEating> wayOfEating ;

        @Schema(name= "content" , example = "글 내용")
        @NotNull
        @Size(min = 1, max = 100000)
        private String content;

        @Schema(name= "anonymity" , example = "익명 여부")
        @NotNull
        private Boolean anonymity;
    }
}
