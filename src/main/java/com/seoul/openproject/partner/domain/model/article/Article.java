package com.seoul.openproject.partner.domain.model.article;

import com.seoul.openproject.partner.domain.model.BaseTimeVersionEntity;
import com.seoul.openproject.partner.domain.model.match.MatchCondition;
import com.seoul.openproject.partner.domain.model.member.Member;
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

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private WayOfEating wayOfEating;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Place place;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private TimeOfEating timeOfEating;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private TypeOfEating typeOfEating;


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
    private Integer participantNum = 1;






    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_AUTHOR_ID", nullable = false, updatable = false)
    private Member memberAuthor;

    @Singular
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    private List<MatchCondition> matchConditions = new ArrayList<>();


    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

//    public static Article of(String title, String content, boolean anonymity, Member memberAuthor){
//        Article.builder()
//            .title(title)
//            .content(content)
//            .anonymity(anonymity)
//            .memberAuthor(memberAuthor)
//            .
//    }
    /********************************* 비니지스 로직 *********************************/

    /********************************* DTO *********************************/
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ArticleOnlyIdResponse {
        private String articleId;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ArticleDto{
        private String memberId;
        private String title;
        private List<TimeOfEating> timeOfEating;
        private List<TypeOfEating> typeOfEating;
        private List<WayOfEating> wayOfEating;
        private String content;
        private Boolean anonymity;
    }
}
