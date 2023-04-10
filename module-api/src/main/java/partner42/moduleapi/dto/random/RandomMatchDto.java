package partner42.moduleapi.dto.random;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.random.RandomMatch;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonTypeInfo(
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "contentCategory",
    use = JsonTypeInfo.Id.NAME,
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MealRandomMatchDto.class, name = "MEAL"),
    @JsonSubTypes.Type(value = StudyRandomMatchDto.class, name = "STUDY")
})
public abstract class RandomMatchDto {

    @Schema(name = "contentCategory", example = "MEAL or STUDY", description = "식사, 공부 글인지 여부")
    @NotNull
    protected ContentCategory contentCategory;

    @Schema(name = "place", example = "SEOCHO(서초 클러스터), GAEPO(개포 클러스터), OUT_OF_CLUSTER(클러스터 외부)", description = "앞에 영어를 배열로 보내면 됨.")
    @NotNull
    protected List<Place> placeList = new ArrayList<>();

    protected void init(){
        if (placeList.isEmpty()){
            placeList = new ArrayList<>(List.of(Place.values()));
        }
    }
    public abstract List<RandomMatch> makeAllAvailRandomMatchesFromRandomMatchDto(Member member);

}