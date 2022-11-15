package com.seoul.openproject.partner.domain.model.matchcondition;

import com.seoul.openproject.partner.domain.model.BaseTimeVersionEntity;
import com.seoul.openproject.partner.domain.model.match.ConditionCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
public class MatchCondition extends BaseTimeVersionEntity {
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
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MatchConditionDto {

        @Builder.Default
        @Schema(name = "place", example = "SEOCHO(서초 클러스터), GAEPO(개포 클러스터), OUT_OF_CLUSTER(클러스터 외부)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<Place> placeList = new ArrayList<>();

        @Builder.Default
        @Schema(name = "timeOfEatingList", example = "BREAKFAST(아침 식사), LUNCH(점심 식사), DUNCH(점저), DINNER(저녁 식사), MIDNIGHT(야식)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<TimeOfEating> timeOfEatingList = new ArrayList<>();

        @Builder.Default
        @Schema(name = "wayOfEatingList", example = " DELIVERY(배달), EATOUT(외식), TAKEOUT(포장)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<WayOfEating> wayOfEatingList = new ArrayList<>();

        @Builder.Default
        @Schema(name = "typeofOfStudyList", example = " INNER_CIRCLE(본 과정), NOT_INNER_CIRCLE(비본 과정)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<TypeOfStudy> typeOfStudyList = new ArrayList<>();

        public static MatchConditionDto of(List<Place> placeList,
            List<TimeOfEating> timeOfEatingList,
            List<WayOfEating> wayOfEatingList,
            List<TypeOfStudy> typeOfStudyList) {
            return MatchConditionDto.builder()
                .placeList(placeList)
                .timeOfEatingList(timeOfEatingList)
                .wayOfEatingList(wayOfEatingList)
                .typeOfStudyList(typeOfStudyList)
                .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MatchConditionRandomMatchDto {

        @Builder.Default
        @Schema(name = "place", example = "SEOCHO(서초 클러스터), GAEPO(개포 클러스터), OUT_OF_CLUSTER(클러스터 외부)", description = "앞에 영어를 배열로 보내면 됨.")
        @NotNull
        private List<Place> placeList = new ArrayList<>();

        @Builder.Default
        @Schema(name = "wayOfEatingList", example = " DELIVERY(배달), EATOUT(외식), TAKEOUT(포장)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<WayOfEating> wayOfEatingList = new ArrayList<>();

        @Builder.Default
        @Schema(name = "typeofOfStudyList", example = " INNER_CIRCLE(본 과정), NOT_INNER_CIRCLE(비본 과정)", description = "앞에 영어를 배열로 보내면 됨.")
        private List<TypeOfStudy> typeOfStudyList = new ArrayList<>();

    }

}
