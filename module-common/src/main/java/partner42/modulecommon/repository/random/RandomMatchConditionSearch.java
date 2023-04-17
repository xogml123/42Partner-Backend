package partner42.modulecommon.repository.random;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;

@Getter
@Setter
@AllArgsConstructor
public class RandomMatchConditionSearch {
    private ContentCategory contentCategory;
    private List<Place> placeList;
    private List<WayOfEating> wayOfEatingList;
    private List<TypeOfStudy> typeOfStudyList;
}
