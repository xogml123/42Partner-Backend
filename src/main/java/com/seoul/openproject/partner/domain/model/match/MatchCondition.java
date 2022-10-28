package com.seoul.openproject.partner.domain.model.match;

import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.member.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
@Table(name = "MATCH_CONDITION")
@Entity
public class MatchCondition {
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
    @Column(name = "MATCH_CONDITION_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/

    @Column(nullable = false, updatable = false)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ConditionCategory conditionCategory;





    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/





    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

    /********************************* 비니지스 로직 *********************************/

    /********************************* DTO *********************************/

}
