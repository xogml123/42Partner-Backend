package com.seoul.openproject.partner.domain.model.match;

import javax.persistence.Entity;

import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "MEAL_RANDOM_MATCH")
@Entity
public class MealRandomMatching extends Matching {
    //********************************* static final 상수 필드 *********************************/

    /**
     * email 뒤에 붙는 문자열
     */


    /********************************* PK 필드 *********************************/
    /**
     * 기본 키
     */
    /********************************* PK가 아닌 필드 *********************************/

    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

    /********************************* 비니지스 로직 *********************************/

    /********************************* DTO *********************************/

}