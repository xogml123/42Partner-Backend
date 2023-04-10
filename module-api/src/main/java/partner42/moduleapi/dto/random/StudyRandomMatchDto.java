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
public class StudyRandomMatchDto extends RandomMatchDto {

//    @Builder.Default
    @NotNull
    @Schema(name = "typeofOfStudyList", example = " INNER_CIRCLE(본 과정), NOT_INNER_CIRCLE(비본 과정)", description = "앞에 영어를 배열로 보내면 됨.")
    private List<TypeOfStudy> typeOfStudyList = new ArrayList<>();

    @Builder
    private StudyRandomMatchDto(ContentCategory contentCategory, List<Place> placeList, List<TypeOfStudy> typeOfStudyList) {
        super(contentCategory, placeList);
        this.typeOfStudyList = typeOfStudyList;
    }
    @Override
    protected void init() {
        super.init();
        if (typeOfStudyList.isEmpty()) {
            typeOfStudyList = new ArrayList<>(List.of(TypeOfStudy.values()));
        }
    }
    @Override
    public final List<RandomMatch> makeAllAvailRandomMatchesFromRandomMatchDto(Member member) {
        //아무 matchCondition필드에 값이 없는 경우 모든 조건으로 변환.
        this.init();
        List<RandomMatch> randomMatches = new ArrayList<>();
        for (Place place : placeList) {
            for (TypeOfStudy typeOfStudy : typeOfStudyList) {
                randomMatches.add(
                    RandomMatch.of(RandomMatchCondition.of(
                        place, typeOfStudy), member));
            }
        }
        return randomMatches;
    }
}
