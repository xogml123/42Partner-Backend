package com.seoul.openproject.partner.domain.model;


import com.seoul.openproject.partner.domain.model.match.Match;
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
@Table(name = "MATCH_CONDITION_MATCH")
@Entity
public class MatchConditionMatch {
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
    @Column(name = "MATCH_CONDITION_MATCH_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/




    /********************************* 비영속 필드 *********************************/


    /********************************* 연관관계 매핑 *********************************/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_ID", nullable = false, updatable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_CONDITION_ID", nullable = false, updatable = false)
    private MatchCondition matchCondition;


    /********************************* 연관관계 편의 메서드 *********************************/


    /********************************* 생성 메서드 *********************************/

    /********************************* 비니지스 로직 *********************************/



    /********************************* DTO *********************************/

}

