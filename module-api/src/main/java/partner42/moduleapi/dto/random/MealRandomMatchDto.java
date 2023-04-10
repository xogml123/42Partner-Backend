package partner42.moduleapi.dto.random;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.random.RandomMatchCondition;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MealRandomMatchDto extends RandomMatchDto{
//    @Builder.Default
    @Schema(name = "wayOfEatingList", example = " DELIVERY(배달), EATOUT(외식), TAKEOUT(포장)", description = "앞에 영어를 배열로 보내면 됨.")
    @NotNull
    private List<WayOfEating> wayOfEatingList = new ArrayList<>();

    @Builder
    private MealRandomMatchDto(ContentCategory contentCategory, List<Place> placeList, List<WayOfEating> wayOfEatingList) {
        super(contentCategory, placeList);
        this.wayOfEatingList = wayOfEatingList;
    }

    @Override
    protected void init(){
        super.init();
        if (wayOfEatingList.isEmpty()){
            wayOfEatingList = new ArrayList<>(List.of(WayOfEating.values()));
        }
    }

    @Override
    public final List<RandomMatch> makeAllAvailRandomMatchesFromRandomMatchDto(Member member){
        //아무 matchCondition필드에 값이 없는 경우 모든 조건으로 변환.
        this.init();
        List<RandomMatch> randomMatches = new ArrayList<>();
        for (Place place : placeList) {
            for (WayOfEating wayOfEating : wayOfEatingList) {
                randomMatches.add(
                    RandomMatch.of(RandomMatchCondition.of(
                        place, wayOfEating), member));
            }
        }
        return randomMatches;
    }
}
