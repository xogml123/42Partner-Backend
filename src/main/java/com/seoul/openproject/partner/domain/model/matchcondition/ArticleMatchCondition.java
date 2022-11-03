package com.seoul.openproject.partner.domain.model.matchcondition;



import com.seoul.openproject.partner.domain.model.BaseTimeVersionEntity;
import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.match.MatchCondition;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "ARTICLE_MATCH_CONDITION")
@Entity
public class ArticleMatchCondition extends BaseTimeVersionEntity {
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
    @Column(name = "ARTICLE_MATCH_CONDITION_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/




    /********************************* 비영속 필드 *********************************/


    /********************************* 연관관계 매핑 *********************************/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID", nullable = false, updatable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_CONDITION_ID", nullable = false, updatable = false)
    private MatchCondition matchCondition;


    /********************************* 연관관계 편의 메서드 *********************************/


    /********************************* 생성 메서드 *********************************/

    public static ArticleMatchCondition of(MatchCondition matchCondition) {
        return ArticleMatchCondition.builder()
                .matchCondition(matchCondition)
                .build();
    }



    /********************************* 비니지스 로직 *********************************/

    public void setArticle(Article article) {
        this.article = article;
        article.getArticleMatchConditions().add(this);
    }

    /********************************* DTO *********************************/

}

