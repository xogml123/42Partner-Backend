package com.seoul.openproject.partner.domain.model.match;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public static MatchCondition of(String value, ConditionCategory conditionCategory) {
        return MatchCondition.builder()
            .value(value)
            .conditionCategory(conditionCategory)
            .build();
    }
    /********************************* 비니지스 로직 *********************************/

    /********************************* DTO *********************************/

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class MatchConditionDto{

        @Schema(name= "value" , example = " DELIVERY(배달), EATOUT(외식), TAKEOUT(포장)", description = "앞에 영어를 값으로 보내면 됨, 장소, 시간, 식사 방식, 메뉴 등이 문자열로 전달.")
        @NotNull
        private String value;

        @Schema(name= "ConditionCategory" , example = "PLACE, TIME_OF_EATING, TYPE_OF_EATING, WAY_OF_EATING", description = "value가 어떤 분류에 속하는지")
        @NotNull
        private ConditionCategory conditionCategory;
    }

}
