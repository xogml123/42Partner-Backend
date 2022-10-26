package com.seoul.openproject.partner.domain.model.opnion;

import com.seoul.openproject.partner.domain.model.BaseTimeVersionEntity;
import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.member.Member;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "OPINION", uniqueConstraints = {
    @UniqueConstraint(name = "API_ID_UNIQUE", columnNames = {"apiId"})
})
@Entity
public class Opinion extends BaseTimeVersionEntity {
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

    @Builder.Default
    @Column(nullable = false)
    private Boolean anonymity = false;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private String parentApiId;

    @Column(nullable = false, updatable = false)
    private Integer level;

    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_AUTHOR_ID", nullable = false, updatable = false)
    private Member memberAuthor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID", nullable = false, updatable = false)
    private Article article;


    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/


    /********************************* 비니지스 로직 *********************************/

}


