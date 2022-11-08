package com.seoul.openproject.partner.domain.model.match;


import com.seoul.openproject.partner.domain.model.BaseEntity;
import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.matchcondition.MatchConditionMatch;
import com.seoul.openproject.partner.domain.model.member.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
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
import lombok.Singular;


@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table(name = "MATCHS", uniqueConstraints = {
    @UniqueConstraint(name = "API_ID_UNIQUE", columnNames = {"apiId"}),
})

@Entity
public class Match extends BaseEntity {
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
    @Column(name = "MATCH_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/

    /**
     * AUTH에 필요한 필드
     */

    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private MatchStatus matchStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ContentCategory contentCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private MethodCategory methodCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID", updatable = false)
    private Article article;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "match")
    @Column(nullable = false, updatable = false)
    private List<MatchMember> matchMembers = new ArrayList<>();




    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/


    @Builder.Default
    @OneToMany(mappedBy = "match", fetch = FetchType.LAZY)
    private List<MatchConditionMatch> matchConditionMatches = new ArrayList<>();

    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

    public static Match of(MatchStatus matchStatus, ContentCategory contentCategory, MethodCategory methodCategory, Article article) {
        return Match.builder()
            .matchStatus(matchStatus)
            .contentCategory(contentCategory)
            .methodCategory(methodCategory)
            .article(article)
            .build();
    }



    /********************************* 비니지스 로직 *********************************/

}

