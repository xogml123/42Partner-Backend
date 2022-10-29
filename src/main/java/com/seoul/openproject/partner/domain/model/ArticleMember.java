package com.seoul.openproject.partner.domain.model;

import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.member.Member;
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
@Table(name = "ARTICLE_MEMBER")
@Entity
public class ArticleMember {

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
    @Column(name = "ARTICLE_MEMBER_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/

    /**
     * AUTH에 필요한 필드
     */

    @Column(name = "IS_AUTHOR", nullable = false, updatable = false)
    private Boolean isAuthor;








    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID", nullable = false, updatable = false)
    private Article article;


    /*********************************  *********************************/


    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

    public static ArticleMember of(Member member, boolean isAuthor) {
        return ArticleMember.builder()
            .member(member)
            .isAuthor(isAuthor)
            .build();
    }

    /********************************* 비니지스 로직 *********************************/

    public void setArticle(Article article) {
        this.article = article;
        article.getArticleMembers().add(this);
    }

    /********************************* DTO *********************************/
}
