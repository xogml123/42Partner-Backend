package com.seoul.openproject.partner.domain.model.activity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "MEAL_ARTICLE_ACTIVITY")
@Entity
public class MealArticleActivity extends Activity{
    //********************************* static final 상수 필드 *********************************/



    /********************************* PK 필드 *********************************/


    /********************************* PK가 아닌 필드 *********************************/

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private MealMatchActivityType mealMatchActivityType;



    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/



    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/


    /********************************* 비니지스 로직 *********************************/

}


