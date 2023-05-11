package partner42.moduleapi.dto.matchcondition;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TimeOfEating;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchConditionDto implements Serializable{

    private static final long serialVersionUID = 1L;

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